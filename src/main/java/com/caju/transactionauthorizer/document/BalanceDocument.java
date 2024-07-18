package com.caju.transactionauthorizer.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "balance")
public class BalanceDocument {

    @Id
    private String account;
    private BigDecimal food;
    private BigDecimal meal;
    private BigDecimal cash;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BigDecimal getFood() {
        return food;
    }

    public void setFood(BigDecimal food) {
        this.food = food;
    }

    public BigDecimal getMeal() {
        return meal;
    }

    public void setMeal(BigDecimal meal) {
        this.meal = meal;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }
}
