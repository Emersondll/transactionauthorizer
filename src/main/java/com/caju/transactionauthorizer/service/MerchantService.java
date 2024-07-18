package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.MerchantDocument;

import java.util.Optional;

public interface MerchantService {

    Optional<MerchantDocument> findById(String id);
}
