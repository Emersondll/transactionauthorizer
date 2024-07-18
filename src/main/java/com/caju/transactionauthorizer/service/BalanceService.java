package com.caju.transactionauthorizer.service;

import java.math.BigDecimal;

public interface BalanceService {
    void performBalance(String account);

    BigDecimal checkBalance(String account);


}
