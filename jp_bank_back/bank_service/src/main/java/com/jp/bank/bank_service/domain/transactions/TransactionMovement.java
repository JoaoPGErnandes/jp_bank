package com.jp.bank.bank_service.domain.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.jp.bank.bank_service.domain.user.BankUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_movements")
public class TransactionMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transation_movement_id")
    private Long transactionMovementId;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private BankUser user;

    @Enumerated(EnumType.STRING)
    private MovementType movementType;

    private BigDecimal amount;

    private LocalDateTime timestamp = LocalDateTime.now();

    public TransactionMovement() {}


    public Long getTransactionMovementId() {
        return transactionMovementId;
    }

    public void setTransactionMovementId(Long transactionMovementId) {
        this.transactionMovementId = transactionMovementId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BankUser getUser() {
        return user;
    }

    public void setUser(BankUser user) {
        this.user = user;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    
}
