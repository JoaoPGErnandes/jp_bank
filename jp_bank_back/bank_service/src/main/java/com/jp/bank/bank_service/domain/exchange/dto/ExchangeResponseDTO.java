package com.jp.bank.bank_service.domain.exchange.dto;

import java.math.BigDecimal;

public record ExchangeResponseDTO(
    String currency,
    BigDecimal amountPurchased,
    BigDecimal priceInBRL,
    BigDecimal totalCost,
    BigDecimal newBalanceBRL
) {}
