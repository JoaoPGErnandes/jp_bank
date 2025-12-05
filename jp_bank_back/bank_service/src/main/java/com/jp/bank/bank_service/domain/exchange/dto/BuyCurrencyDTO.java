package com.jp.bank.bank_service.domain.exchange.dto;

import java.math.BigDecimal;

public record BuyCurrencyDTO(
    String currency,
    BigDecimal priceInBRL,
    BigDecimal amount
) {}
