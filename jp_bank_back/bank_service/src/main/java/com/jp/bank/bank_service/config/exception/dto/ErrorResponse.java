package com.jp.bank.bank_service.config.exception.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp
) {

}
