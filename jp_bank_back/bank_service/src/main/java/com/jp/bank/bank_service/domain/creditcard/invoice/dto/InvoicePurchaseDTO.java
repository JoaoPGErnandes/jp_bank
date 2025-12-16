package com.jp.bank.bank_service.domain.creditcard.invoice.dto;

import java.math.BigDecimal;

public record InvoicePurchaseDTO(
    Long purchaseId,
    String description,
    BigDecimal installmentAmount,
    int installmentNumber,
    int totalInstallemnts
) {

}
