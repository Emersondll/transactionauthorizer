package com.caju.transactionauthorizer.repository;

import com.caju.transactionauthorizer.document.TransactionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<TransactionDocument, String> {
}