package com.jp.bank.bank_service.domain.creditcard.dto;

import java.time.LocalDate;

public record CreditCardShowInfoDTO(
    String cardNumber,
    String cvv,
    LocalDate expiry,
    String nickname
) {}
