package com.jp.bank.bank_service.domain.creditcard;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.jp.bank.bank_service.domain.creditcard.invoice.CreditCardInvoice;
import com.jp.bank.bank_service.domain.user.BankUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class CreditCardTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private CreditCard card;

    @ManyToOne
    private BankUser user;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = true)
    private CreditCardInvoice invoice;

    private BigDecimal installmentAmount;
    private Integer installmentNumber;
    private Integer totalInstallments;

    private LocalDate dueDate;
    private boolean paid = false;

    private String description;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private CreditCardPurchase purchase;

    public CreditCardTransaction() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditCard getCard() {
        return card;
    }

    public void setCard(CreditCard card) {
        this.card = card;
    }

    public BankUser getUser() {
        return user;
    }

    public void setUser(BankUser user) {
        this.user = user;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public Integer getTotalInstallments() {
        return totalInstallments;
    }

    public void setTotalInstallments(Integer totalInstallments) {
        this.totalInstallments = totalInstallments;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String matadata) {
        this.metadata = matadata;
    }

    public CreditCardPurchase getPurchase() {
        return purchase;
    }

    public void setPurchase(CreditCardPurchase purchase) {
        this.purchase = purchase;
    }

    public CreditCardInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(CreditCardInvoice invoice) {
        this.invoice = invoice;
    }

}
