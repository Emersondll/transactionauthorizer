package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;

public interface TransactionService {
    TransactionCodeModel performTransaction(TransactionModel transactionModel);
}
