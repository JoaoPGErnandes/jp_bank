package com.jp.bank.bank_service.domain.creditcard.invoice;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.bank.bank_service.domain.user.BankUser;

public interface CreditCardInvoiceRepository extends JpaRepository<CreditCardInvoice, Long>{

    Optional<CreditCardInvoice> findByuserAndClosingDate(BankUser user, LocalDate closingDate);

    Optional<CreditCardInvoice> findByUserAndPaidFalse(BankUser user);
}
