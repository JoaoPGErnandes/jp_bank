package com.jp.bank.bank_service.domain.creditcard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jp.bank.bank_service.domain.user.BankUser;

public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, Long>{

    @Query("""
            SELECT c FROM CreditCardTransaction c
            WHERE c.user.id = :userId
            AND c.invoice IS NULL
            AND c.dueDate BETWEEN :start AND :end
            """)
    List<CreditCardTransaction> findInstallmentWithoutInvoice(Long userId, LocalDate start, LocalDate end);

    @Query("""
            SELECT COALESCE(SUM(t.installmentAmount), 0)
            FROM CreditCardTransaction t
            WHERE t.user = :user
            AND t.dueDate >= :start
            AND t.dueDate < :end
            """)
    BigDecimal sumInvoiceAmount(
        @Param("user") BankUser user,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}
