package com.caju.transactionauthorizer.repository;

import com.caju.transactionauthorizer.document.MerchantDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MerchantRepository extends MongoRepository<MerchantDocument, String> {
}