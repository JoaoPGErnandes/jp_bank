package com.jp.bank.bank_service.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterDTO(
    @NotBlank(message = "Owner name is required")
    @Size(max = 100)
    String ownerName,

    @NotBlank(message = "CPF is required")
    @Size(min = 11, max = 11, message = "CPF must have 11 digits")
    String ownerCpf,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    String email,

    @NotBlank(message = "Phone is required")
    @Size(min = 10, max = 14)
    String phone,

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,

    @NotBlank(message = "Account type is required")
    @Size(max = 20)
    String accountType
) {

}
