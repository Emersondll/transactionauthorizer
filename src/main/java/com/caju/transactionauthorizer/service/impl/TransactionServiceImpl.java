package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;
import com.caju.transactionauthorizer.repository.TransactionRepository;
import com.caju.transactionauthorizer.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository repository;

    @Override
    public TransactionCodeModel performTransaction(TransactionModel transactionModel) {

        return null;
    }
}