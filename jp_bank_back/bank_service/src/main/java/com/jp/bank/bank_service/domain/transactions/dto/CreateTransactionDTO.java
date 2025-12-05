package com.jp.bank.bank_service.domain.transactions.dto;

import java.math.BigDecimal;

import com.jp.bank.bank_service.domain.transactions.TransactionType;

public record CreateTransactionDTO(
    TransactionType type,
    BigDecimal amount,
    String description,
    String receiverAgencyNumber,
    String receiverAccountNumber,
    String metadata
) {}
