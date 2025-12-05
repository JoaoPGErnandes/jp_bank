package com.jp.bank.bank_service.domain.user.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BankUserResponseDTO(
    Long bankId,
    String ownerName,
    String email,
    String accountNumber,
    String agencyNumber,
    BigDecimal balance,
    String accountType,
    LocalDateTime createdAt,
    String status
) {}
