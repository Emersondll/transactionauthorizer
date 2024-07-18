package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.document.MerchantDocument;
import com.caju.transactionauthorizer.document.TransactionDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;
import com.caju.transactionauthorizer.enums.TransactionStatusCode;
import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;
import com.caju.transactionauthorizer.repository.TransactionRepository;
import com.caju.transactionauthorizer.service.BalanceService;
import com.caju.transactionauthorizer.service.MerchantCategoryCodesService;
import com.caju.transactionauthorizer.service.MerchantService;
import com.caju.transactionauthorizer.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantCategoryCodesService categoryCodesService;

    @Override
    @Transactional
    public TransactionCodeModel performTransaction(TransactionModel transactionModel) {
        String accountId = transactionModel.account();
        BigDecimal amount = transactionModel.totalAmount();

        try {
            // Get balance
            Optional<BalanceDocument> balanceOptional = balanceService.findByAccount(accountId);
            // Determine MCC category
            String mccCategoryNumber = determineMccCategory(transactionModel);
            CategoryCodeName categoryCodeName = categoryCodesService.checkCategory(mccCategoryNumber);

            // Update wallet balance based on category
            TransactionCodeModel transactionCodeModel = updateWalletBalance(balanceOptional.get(), categoryCodeName, amount);
            if (transactionCodeModel.code().equals(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode())) {
                return transactionCodeModel;
            }

            // Save updated balance and transaction
            balanceService.save(balanceOptional.get());
            saveTransaction(transactionModel, accountId, amount, mccCategoryNumber);

            return transactionCodeModel;
        } catch (Exception e) {
            return new TransactionCodeModel(TransactionStatusCode.PROCESSING_ERROR.getCode());
        }
    }

    private String determineMccCategory(TransactionModel transactionModel) {
        Optional<MerchantDocument> merchantOptional = merchantService.findByName(transactionModel.merchant());
        if (merchantOptional.isPresent()) {
            return merchantOptional.get().getMcc();
        } else {
            Optional<MerchantCategoryCodesDocument> categoryCodesOptional = categoryCodesService.findByCode(transactionModel.mcc());
            return categoryCodesOptional.map(MerchantCategoryCodesDocument::getCode).orElse(null);
        }
    }

    private TransactionCodeModel updateWalletBalance(BalanceDocument balance, CategoryCodeName categoryCodeName, BigDecimal amount) {
        boolean updated = switch (categoryCodeName) {
            case FOOD ->
                    updateBalanceWithFallback(balance::getFood, balance::setFood, balance::getCash, balance::setCash, amount);
            case MEAL ->
                    updateBalanceWithFallback(balance::getMeal, balance::setMeal, balance::getCash, balance::setCash, amount);
            case CASH ->
                    updateBalanceWithFallback(balance::getCash, balance::setCash, balance::getCash, balance::setCash, amount);
        };

        if (!updated) {
            return new TransactionCodeModel(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode());
        }

        return new TransactionCodeModel(TransactionStatusCode.APPROVED.getCode());
    }

    private boolean updateBalanceWithFallback(Supplier<BigDecimal> primaryGetter, Consumer<BigDecimal> primarySetter,
                                              Supplier<BigDecimal> fallbackGetter, Consumer<BigDecimal> fallbackSetter, BigDecimal amount) {
        // Update primary balance
        BigDecimal primaryBalance = primaryGetter.get();
        BigDecimal updatedPrimaryBalance = subtractAmount(primaryBalance, amount);

        if (updatedPrimaryBalance != null) {
            primarySetter.accept(updatedPrimaryBalance);
            return true;
        }

        // Update fallback balance if primary balance is insufficient
        BigDecimal fallbackBalance = fallbackGetter.get();
        BigDecimal updatedFallbackBalance = subtractAmount(fallbackBalance, amount);

        if (updatedFallbackBalance != null) {
            fallbackSetter.accept(updatedFallbackBalance);
            return true;
        }

        return false;
    }

    private BigDecimal subtractAmount(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) >= 0) {
            return balance.subtract(amount);
        }
        return null;
    }

    private void saveTransaction(TransactionModel transactionModel, String accountId, BigDecimal amount, String mccCategoryNumber) {
        TransactionDocument transactionDocument = new TransactionDocument(
                UUID.randomUUID().toString(), accountId, amount, transactionModel.merchant(), mccCategoryNumber, Timestamp.valueOf(LocalDateTime.now()));
        transactionRepository.save(transactionDocument);
    }
}
