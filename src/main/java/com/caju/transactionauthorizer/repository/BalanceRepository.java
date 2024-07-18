package com.caju.transactionauthorizer.repository;

import com.caju.transactionauthorizer.document.BalanceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BalanceRepository extends MongoRepository<BalanceDocument, String> {
}