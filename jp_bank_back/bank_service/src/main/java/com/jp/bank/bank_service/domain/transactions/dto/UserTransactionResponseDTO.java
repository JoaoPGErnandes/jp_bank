package com.jp.bank.bank_service.domain.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.jp.bank.bank_service.domain.transactions.MovementType;
import com.jp.bank.bank_service.domain.transactions.TransactionStatus;
import com.jp.bank.bank_service.domain.transactions.TransactionType;

public record UserTransactionResponseDTO(
    Long transactionId,
    TransactionType type,
    TransactionStatus status,
    BigDecimal amount,
    String description,
    LocalDateTime createdAt,
    Long movimentId,
    MovementType movementType
) {}
