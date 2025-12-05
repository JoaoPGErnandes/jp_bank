package com.jp.bank.bank_service.domain.user.dto;

import java.math.BigDecimal;
import java.util.List;

public record BankUserMeDTO(
    Long id,
    String name,
    String email,
    String cpf,
    BigDecimal balance,
    String accountNumber,
    String agencyNumber,
    String status,
    List<BankUserCurrencyDTO> currencies
) {}
