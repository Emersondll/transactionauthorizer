package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.repository.BalanceRepository;
import com.caju.transactionauthorizer.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceServiceImpl implements BalanceService {


    @Autowired
    private BalanceRepository repository;


    @Override
    public void performBalance(String account) {

    }

    @Override
    public BigDecimal checkBalance(String account) {
        return null;
    }
}
