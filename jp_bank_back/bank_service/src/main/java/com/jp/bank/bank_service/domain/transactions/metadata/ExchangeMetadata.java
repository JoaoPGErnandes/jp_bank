package com.jp.bank.bank_service.domain.transactions.metadata;

import java.math.BigDecimal;

public class ExchangeMetadata {

    private String currency;
    private BigDecimal amountPurchased;
    private BigDecimal priceInBRL;

    public ExchangeMetadata(String currency, BigDecimal amountPurchased, BigDecimal priceInBRL) {
        this.currency = currency;
        this.amountPurchased = amountPurchased;
        this.priceInBRL = priceInBRL;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmountPurchased() {
        return amountPurchased;
    }

    public BigDecimal getPriceInBRL() {
        return priceInBRL;
    }

}
