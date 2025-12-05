package com.jp.bank.bank_service.domain.user;

public enum BankUserRole {
    ADMIN("admin"),
    USER("user");

    private String role;

    BankUserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
