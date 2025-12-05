package com.jp.bank.bank_service.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankUserCurrencyBalanceRepository extends JpaRepository<BankUserCurrencyBalance, Long>{

    Optional<BankUserCurrencyBalance> findByUserAndCurrency(BankUser user, String currency);
}
