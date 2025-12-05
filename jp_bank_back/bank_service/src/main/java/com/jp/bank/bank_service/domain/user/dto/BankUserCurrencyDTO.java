package com.jp.bank.bank_service.domain.user.dto;

import java.math.BigDecimal;

public record BankUserCurrencyDTO(
    String currency,
    BigDecimal balance
) {

}
