package com.caju.transactionauthorizer.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TransactionModel(@JsonProperty("account") String account,
                               @JsonProperty("totalAmount") BigDecimal totalAmount,
                               @JsonProperty("mcc") String mcc,
                               @JsonProperty("merchant") String merchant
) {
}
