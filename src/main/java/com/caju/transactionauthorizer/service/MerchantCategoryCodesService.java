package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;

import java.util.Optional;

public interface MerchantCategoryCodesService {
    Optional<MerchantCategoryCodesDocument> findById(String id);
}
