package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.repository.MerchantCategoryCodesRepository;
import com.caju.transactionauthorizer.service.MerchantCategoryCodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantCategoryCodesServiceImpl implements MerchantCategoryCodesService {


    @Autowired
    private MerchantCategoryCodesRepository repository;


    @Override
    public Optional<MerchantCategoryCodesDocument> findById(String id) {
        return repository.findById(id);
    }
}
