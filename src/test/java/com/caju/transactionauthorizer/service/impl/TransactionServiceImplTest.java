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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

import static com.caju.transactionauthorizer.service.impl.TestConstants.ACCOUNT;
import static com.caju.transactionauthorizer.service.impl.TestConstants.ACCOUNT_ID;
import static com.caju.transactionauthorizer.service.impl.TestConstants.AMOUNT_0;
import static com.caju.transactionauthorizer.service.impl.TestConstants.AMOUNT_100;
import static com.caju.transactionauthorizer.service.impl.TestConstants.AMOUNT_200;
import static com.caju.transactionauthorizer.service.impl.TestConstants.AMOUNT_50;
import static com.caju.transactionauthorizer.service.impl.TestConstants.ID;
import static com.caju.transactionauthorizer.service.impl.TestConstants.MCC;
import static com.caju.transactionauthorizer.service.impl.TestConstants.MERCHANT;
import static com.caju.transactionauthorizer.service.impl.TestConstants.UNEXPECTED_ERROR_MSG;
import static com.caju.transactionauthorizer.service.impl.TestConstants.VERSION_1L;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BalanceService balanceService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private MerchantCategoryCodesService categoryCodesService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPerformTransactionInsufficientFunds() {
        TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);
        BalanceDocument balance = new BalanceDocument(ID, ACCOUNT, AMOUNT_0, AMOUNT_0, AMOUNT_50, VERSION_1L);
        MerchantDocument merchantDocument = new MerchantDocument(ID, MERCHANT, MERCHANT);

        when(balanceService.findByAccount(anyString())).thenReturn(Optional.of(balance));
        when(categoryCodesService.checkCategory(anyString())).thenReturn(CategoryCodeName.CASH);
        when(merchantService.findByName(anyString())).thenReturn(Optional.of(merchantDocument));

        TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode(), result.code());
    }

    @Test
    void testPerformTransactionProcessingError() {
        TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        when(balanceService.findByAccount(anyString())).thenReturn(Optional.empty());

        TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
    }

    @Test
    void testPerformTransactionSuccess() {
        TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);
        BalanceDocument balance = new BalanceDocument(ID, ACCOUNT, AMOUNT_0, AMOUNT_0, AMOUNT_200, VERSION_1L);
        MerchantDocument merchantDocument = new MerchantDocument(ID, MERCHANT, MERCHANT);

        when(balanceService.findByAccount(anyString())).thenReturn(Optional.of(balance));
        when(categoryCodesService.checkCategory(anyString())).thenReturn(CategoryCodeName.CASH);
        when(merchantService.findByName(anyString())).thenReturn(Optional.of(merchantDocument));

        TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    void testDetermineMccCategory() {
        TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);
        MerchantDocument merchant = new MerchantDocument(ID, MERCHANT, MCC);

        when(merchantService.findByName(anyString())).thenReturn(Optional.of(merchant));

        String result = transactionService.determineMccCategory(transactionModel);

        assertEquals(MCC, result);
    }

    @Test
    void testUpdateWalletBalanceSuccess() {
        BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_0, AMOUNT_200, VERSION_1L);

        TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.CASH, AMOUNT_100);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    void testUpdateWalletBalanceInsufficientFunds() {
        BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_0, AMOUNT_50, VERSION_1L);

        TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.CASH, AMOUNT_100);

        assertEquals(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode(), result.code());
    }

    @Test
    void testUpdateBalanceWithFallback() {
        BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_0, AMOUNT_200, VERSION_1L);

        boolean result = transactionService.updateBalanceWithFallback(balance::getCash, balance::setCash, balance::getFood, balance::setFood, AMOUNT_100);

        assertTrue(result);
        assertEquals(AMOUNT_100, balance.getCash());
    }

    @Test
    void testSaveTransaction() {
        TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        transactionService.saveTransaction(transactionModel, ACCOUNT_ID, AMOUNT_100, MCC);

        verify(transactionRepository, times(1)).save(any(TransactionDocument.class));
    }

    @Test
    void testPerformTransactionOptimisticLockingFailure() {
        TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        when(balanceService.findByAccount(ACCOUNT_ID))
                .thenThrow(new OptimisticLockingFailureException(UNEXPECTED_ERROR_MSG));

        TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
        verify(balanceService, times(1)).findByAccount(ACCOUNT_ID);
    }


    @Test
    void testPerformTransactionGenericException() {
        TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        when(balanceService.findByAccount(ACCOUNT_ID)).thenThrow(new RuntimeException(UNEXPECTED_ERROR_MSG));

        TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
    }

    @Test
    void testUpdateWalletBalanceFoodCategory() {
        BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_100, AMOUNT_0, AMOUNT_0, VERSION_1L);

        TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.FOOD, AMOUNT_50);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    void testUpdateWalletBalanceMealCategory() {
        BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_100, AMOUNT_0, VERSION_1L);

        TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.MEAL, AMOUNT_50);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    void testDetermineMccCategoryFromCategoryCodes() {
        TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        when(merchantService.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryCodesService.findByCode(anyString())).thenReturn(Optional.of(new MerchantCategoryCodesDocument(ID, MCC, CategoryCodeName.FOOD)));

        String result = transactionService.determineMccCategory(transactionModel);

        assertEquals(MCC, result);
    }

    @Test
    void testUpdateBalanceWithFallbackWithNonNullFallback() {
        BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_0, AMOUNT_100, 1L);

        boolean result = transactionService.updateBalanceWithFallback(
                balance::getFood,
                balance::setFood,
                balance::getCash,
                balance::setCash,
                AMOUNT_50
        );

        assertTrue(result);
        assertEquals(AMOUNT_50, balance.getCash());
        assertEquals(AMOUNT_0, balance.getFood());
    }


}
