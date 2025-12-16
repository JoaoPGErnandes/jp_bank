package com.jp.bank.bank_service.domain.transactions.metadata;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreditCardMetadata {

    private Long creditCardId;
    private int installments;
    private Double interestRate;
    private BigDecimal amountWitoutInterest;
    private BigDecimal installmentValue;
    private LocalDate dueDate;

    public CreditCardMetadata() {}

    public CreditCardMetadata(Long creditCardId, int installments, Double interestRate, BigDecimal amountWithoutInterest, BigDecimal installmentValue, LocalDate dueDate) {
        this.creditCardId = creditCardId;
        this.installments = installments;
        this.interestRate = interestRate;
        this.amountWitoutInterest = amountWithoutInterest;
        this.installmentValue = installmentValue;
        this.dueDate = dueDate;
    }

    public Long getCreditCardId() {
        return creditCardId;
    }

    public int getInstallments() {
        return installments;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public BigDecimal getAmountWitoutInterest() {
        return amountWitoutInterest;
    }

    public BigDecimal getInstallmentValue() {
        return installmentValue;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    
}
