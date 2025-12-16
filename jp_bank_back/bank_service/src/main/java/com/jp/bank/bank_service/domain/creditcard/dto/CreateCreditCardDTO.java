package com.jp.bank.bank_service.domain.creditcard.dto;

public record CreateCreditCardDTO (
    boolean virtual,
    String nickname,
    int closingDay
) {

}
