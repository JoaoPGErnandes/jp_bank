package com.jp.bank.bank_service.domain.creditcard.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreditCardInvoiceResponseDTO(
    Long invoiceId,
    LocalDate closingDate,
    LocalDate dueDate,
    BigDecimal totalAmount,
    boolean paid,
    PaymentInfoDTO payment,
    List<InvoicePurchaseDTO> purchases
) {

}
