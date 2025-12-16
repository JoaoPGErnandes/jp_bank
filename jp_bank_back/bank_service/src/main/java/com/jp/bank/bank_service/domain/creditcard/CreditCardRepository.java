package com.jp.bank.bank_service.domain.creditcard;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.bank.bank_service.domain.user.BankUser;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long>{

    Optional<CreditCard> findByCardNumber(String cardNumber);

    Optional<CreditCard> findByCardNumberAndCvvAndExpiry(String cardNumber, String cvv, LocalDate expiry);

    Optional<List<CreditCard>> findByOwner(BankUser user);
}
