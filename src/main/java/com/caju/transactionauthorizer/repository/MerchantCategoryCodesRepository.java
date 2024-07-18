package com.caju.transactionauthorizer.repository;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MerchantCategoryCodesRepository extends MongoRepository<MerchantCategoryCodesDocument, String> {
}