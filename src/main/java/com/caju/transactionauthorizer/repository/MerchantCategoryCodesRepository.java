package com.caju.transactionauthorizer.repository;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MerchantCategoryCodesRepository extends MongoRepository<MerchantCategoryCodesDocument, String> {
    Optional<MerchantCategoryCodesDocument> findByCode(final String mcc);

}