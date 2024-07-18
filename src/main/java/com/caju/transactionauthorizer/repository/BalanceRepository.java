package com.caju.transactionauthorizer.repository;

import com.caju.transactionauthorizer.document.BalanceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BalanceRepository extends MongoRepository<BalanceDocument, String> {
    Optional<BalanceDocument> findByAccount(final String account);
}