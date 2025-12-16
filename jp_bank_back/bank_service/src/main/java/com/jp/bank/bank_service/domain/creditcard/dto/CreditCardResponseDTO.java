package com.jp.bank.bank_service.domain.creditcard.dto;

import java.time.LocalDate;

public record CreditCardResponseDTO (
    Long cardId,
    Long ownerId,
    String cardNumber,
    String cvv,
    LocalDate expiry,
    boolean virtual,
    String nickname,
    int closingDay,
    boolean active
) {

}
