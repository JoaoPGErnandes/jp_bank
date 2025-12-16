package com.jp.bank.bank_service.domain.creditcard.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentInfoDTO(
    Long transactionId,
    BigDecimal amount,
    LocalDateTime paidAt
) {

}
