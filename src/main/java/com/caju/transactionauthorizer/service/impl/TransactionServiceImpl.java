package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.document.MerchantDocument;
import com.caju.transactionauthorizer.document.TransactionDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;
import com.caju.transactionauthorizer.enums.TransactionStatusCode;
import com.caju.transactionauthorizer.exception.BalanceException;
import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;
import com.caju.transactionauthorizer.repository.TransactionRepository;
import com.caju.transactionauthorizer.service.BalanceService;
import com.caju.transactionauthorizer.service.MerchantCategoryCodesService;
import com.caju.transactionauthorizer.service.MerchantService;
import com.caju.transactionauthorizer.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
    public TransactionCodeModel performTransaction(TransactionModel transactionModel) {
        String accountId = transactionModel.account();
        BigDecimal amount = transactionModel.totalAmount();
        String mcc = null;


        Optional<BalanceDocument> balanceOptional = balanceService.findByAccount(accountId);
        if (balanceOptional.isEmpty()) {
            throw new BalanceException("Balance not found for account: " + accountId);
        }

        BalanceDocument balance = balanceOptional.get();
        BigDecimal updatedBalance = null;

        // ***********************************************

        Optional<MerchantDocument> optionalMerchantDocument = merchantService.findByName(transactionModel.merchant());
        if (optionalMerchantDocument.isPresent()) {
            mcc = optionalMerchantDocument.get().getMcc();
        } else {
            Optional<MerchantCategoryCodesDocument> optionalMerchantCategoryCodesDocument = categoryCodesService.findByCode(transactionModel.mcc());
            if (optionalMerchantCategoryCodesDocument.isPresent()) {
                mcc = optionalMerchantCategoryCodesDocument.get().getCode();
            }
        }
        //****************************************************

        CategoryCodeName categoryCodeName = categoryCodesService.checkCategory(mcc);

        if (categoryCodeName.equals(CategoryCodeName.FOOD)) {
            updatedBalance = updateBalance(balance.getFood(), amount);
            balance.setFood(updatedBalance);
        } else if (categoryCodeName.equals(CategoryCodeName.MEAL)) {
            updatedBalance = updateBalance(balance.getMeal(), amount);
            balance.setMeal(updatedBalance);
        } else {
            updatedBalance = updateBalance(balance.getCash(), amount);
            balance.setCash(updatedBalance);
        }

        if (updatedBalance == null) {
            updatedBalance = updateBalance(balance.getCash(), amount);
            balance.setCash(updatedBalance);
            if (updatedBalance == null) {
                return new TransactionCodeModel(TransactionStatusCode.INSUFFICIENT_FUNDS);
            }
        }

        balanceService.save(balance);
        TransactionDocument transactionDocument = new TransactionDocument(
                UUID.randomUUID().toString(), accountId, amount, transactionModel.merchant(), mcc, Timestamp.valueOf(LocalDateTime.now()));
        transactionRepository.save(transactionDocument);

        return new TransactionCodeModel(TransactionStatusCode.APPROVED);
    }


    private BigDecimal updateBalance(BigDecimal currentBalance, BigDecimal amount) {
        if (currentBalance.compareTo(amount) >= 0) {
            return currentBalance.subtract(amount);
        }
        return null;
    }
}
