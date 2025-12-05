package com.jp.bank.bank_service.domain.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class BankUserCurrencyBalanceService {

    private final BankUserCurrencyBalanceRepository bankUserCurrencyBalanceRepository;

    public BankUserCurrencyBalanceService(BankUserCurrencyBalanceRepository bankUserCurrencyBalanceRepository) {
        this.bankUserCurrencyBalanceRepository = bankUserCurrencyBalanceRepository;
    }

    public Optional<BankUserCurrencyBalance> getByUserAndCurrency(BankUser user, String currency) {
        return bankUserCurrencyBalanceRepository.findByUserAndCurrency(user, currency);
    }

    public BankUserCurrencyBalance save(BankUserCurrencyBalance currency) {
        return bankUserCurrencyBalanceRepository.save(currency);
    }
}
