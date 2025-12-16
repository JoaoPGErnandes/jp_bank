package com.jp.bank.bank_service.domain.creditcard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditCardPurchaseResponseDTO(
    Long purchaseId,
    String description,
    BigDecimal totalAmount,
    Integer totalInstallments,
    LocalDate purchaseDate,
    String cardNickname
) {

}
