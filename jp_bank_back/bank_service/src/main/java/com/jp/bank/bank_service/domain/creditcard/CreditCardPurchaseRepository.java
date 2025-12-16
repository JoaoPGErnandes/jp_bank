package com.jp.bank.bank_service.domain.creditcard;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.bank.bank_service.domain.user.BankUser;

public interface CreditCardPurchaseRepository extends JpaRepository<CreditCardPurchase, Long>{

    List<CreditCardPurchase> findByUser(BankUser user);
}
