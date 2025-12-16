package com.jp.bank.bank_service.domain.user;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.security.core.userdetails.UserDetails;

public interface BankUserRepository extends JpaRepository<BankUser, Long>{

    //Voltar pra UserDetails se der algo errado
    Optional<BankUser> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByOwnerCpf(String cpf);

    Optional<BankUser> findByAccountNumber(String accountNumber);

    Optional<BankUser> findByAgencyNumberAndAccountNumber(String agencyNumber, String accountNumber);

    List<BankUser> findByCreditLimitGreaterThan(BigDecimal limit);
}
