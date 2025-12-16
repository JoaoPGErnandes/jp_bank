package com.jp.bank.bank_service.domain.creditcard.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.jp.bank.bank_service.domain.creditcard.CreditCardTransaction;
import com.jp.bank.bank_service.domain.transactions.Transaction;
import com.jp.bank.bank_service.domain.user.BankUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class CreditCardInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    private BankUser user;

    private LocalDate closingDate;
    private LocalDate dueDate;

    private BigDecimal totalAmount;

    private boolean paid = false;

    @OneToOne
    private Transaction paymentTransaction;

    @OneToMany(mappedBy = "invoice", cascade = { CascadeType.MERGE, CascadeType.REFRESH})
    private List<CreditCardTransaction> installments = new ArrayList<>();

    public CreditCardInvoice() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankUser getUser() {
        return user;
    }

    public void setUser(BankUser user) {
        this.user = user;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Transaction getPaymentTransaction() {
        return paymentTransaction;
    }

    public void setPaymentTransaction(Transaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    public List<CreditCardTransaction> getInstallments() {
        return installments;
    }

    public void setInstallments(List<CreditCardTransaction> installments) {
        this.installments = installments;
    }

}
