package com.jp.bank.bank_service.domain.transactions.metadata;

public class CreditCardInvoiceMetadata {

    private Long invoiceId;

    public CreditCardInvoiceMetadata(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

}
