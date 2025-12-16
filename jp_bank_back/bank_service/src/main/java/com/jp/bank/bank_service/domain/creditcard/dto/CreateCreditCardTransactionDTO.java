package com.jp.bank.bank_service.domain.creditcard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCreditCardTransactionDTO(
    String cardNumber,
    String cvv,
    LocalDate cardExpiry,
    BigDecimal installmentAmount,
    int totalInstallments,
    String description
) {

}
