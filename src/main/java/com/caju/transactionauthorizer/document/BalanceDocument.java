package com.caju.transactionauthorizer.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "balance")
public class BalanceDocument {

    @Id
    private String id;
    private String account;
    private BigDecimal food;
    private BigDecimal meal;
    private BigDecimal cash;

    @Version
    private Long version;

    public BalanceDocument() {
    }

    public BalanceDocument(String id, String account, BigDecimal food, BigDecimal meal, BigDecimal cash, Long version) {
        this.id = id;
        this.account = account;
        this.food = food;
        this.meal = meal;
        this.cash = cash;
        this.version = version;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
