package com.caju.transactionauthorizer.repository;

import com.caju.transactionauthorizer.document.MerchantDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MerchantRepository extends MongoRepository<MerchantDocument, String> {
    Optional<MerchantDocument> findByName(final String merchant);

}