package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;

import java.util.Optional;

public interface MerchantCategoryCodesService {
    Optional<MerchantCategoryCodesDocument> findByCode(final String mcc);

    CategoryCodeName checkCategory(String mcc);
}
