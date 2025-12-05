package com.jp.bank.bank_service.domain.transactions;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{

    List<Transaction> findByMovementsUserBankId(Long userId);
}
