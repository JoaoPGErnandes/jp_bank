package com.jp.bank.bank_service.domain.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.jp.bank.bank_service.domain.transactions.MovementType;

public record TransactionMovementDTO(
    Long movementId,
    Long userId,
    MovementType movementType,
    BigDecimal amount,
    LocalDateTime timestamp
) {}
