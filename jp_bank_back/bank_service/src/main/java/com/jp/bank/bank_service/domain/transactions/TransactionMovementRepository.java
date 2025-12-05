package com.jp.bank.bank_service.domain.transactions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionMovementRepository extends JpaRepository<TransactionMovement, Long>{

}
