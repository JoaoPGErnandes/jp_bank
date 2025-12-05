package com.jp.bank.bank_service.domain.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.jp.bank.bank_service.domain.transactions.TransactionStatus;
import com.jp.bank.bank_service.domain.transactions.TransactionType;

public record TransactionResponseDTO(
    Long transactionId,
    TransactionType type,
    TransactionStatus status,
    BigDecimal amount,
    String description,
    LocalDateTime createdAt,
    List<TransactionMovementDTO> movements
) {}
