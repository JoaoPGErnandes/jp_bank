package com.jp.bank.bank_service.domain.user;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jp.bank.bank_service.auth.TokenService;
import com.jp.bank.bank_service.auth.dto.AuthRegisterDTO;
import com.jp.bank.bank_service.domain.user.dto.BankUserCurrencyDTO;
import com.jp.bank.bank_service.domain.user.dto.BankUserMeDTO;
import com.jp.bank.bank_service.domain.user.dto.BankUserResponseDTO;
import com.jp.bank.bank_service.domain.user.dto.PasswordCheckDTO;

@Service
public class BankUserService {

    private final BankUserRepository bankUserRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public BankUserService(BankUserRepository bankUserRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.bankUserRepository = bankUserRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public BankUserResponseDTO create(AuthRegisterDTO data) {

        if(bankUserRepository.existsByEmail(data.email())) {
            throw new RuntimeException("Email already registered");
        }

        if(bankUserRepository.existsByOwnerCpf(data.ownerCpf())) {
            throw new RuntimeException("CPF already registered");
        }

        BankUser user = new BankUser();
        user.setOwnerName(data.ownerName());
        user.setOwnerCpf(data.ownerCpf());
        user.setEmail(data.email());
        user.setPhone(data.phone());
        user.setPassword(new BCryptPasswordEncoder().encode(data.password()));
        user.setAccountType(data.accountType());

        user.setAgencyNumber("0001");
        user.setAccountNumber(generateUniqueAccountNumber());
        user.setBalance(BigDecimal.ZERO);
        user.setStatus("ACTIVE");
        user.setRole(BankUserRole.USER);

        BankUser saved = bankUserRepository.save(user);
        return toResponse(saved);
    }

    private String generateAccountNumber() {
        int number = 1000000 + new Random().nextInt(9000000);
        int dv = number % 10;
        return number + "-" + dv;
    }

    private String generateUniqueAccountNumber() {
        while (true) {
            String acc = generateAccountNumber();
            if(!bankUserRepository.findByAccountNumber(acc).isPresent()) {
                return acc;
            }
        }
    }

    private BankUserResponseDTO toResponse(BankUser user) {
        return new BankUserResponseDTO(
            user.getBankId(),
            user.getOwnerName(),
            user.getEmail(),
            user.getAccountNumber(),
            user.getAgencyNumber(),
            user.getBalance(),
            user.getAccountType(),
            user.getCreatedAt(),
            user.getStatus()
        );
    }

    public BankUser getByEmail(String email) {
        return bankUserRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public BankUser getById(Long id) {
        return bankUserRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public BankUser getByAgencyNumberAndAccountNumber(String agencyNumber, String accountNumber) {
        return bankUserRepository.findByAgencyNumberAndAccountNumber(agencyNumber, accountNumber).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public BankUserMeDTO getUserMe(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        BankUser user = getById(userId);

        List<BankUserCurrencyDTO> currencies = user.getCurrencies()
                                                .stream()
                                                .map(c -> new BankUserCurrencyDTO(
                                                    c.getCurrency(),
                                                    c.getBalance()
                                                )).toList();

        return new BankUserMeDTO(
            user.getBankId(),
            user.getOwnerName(),
            user.getEmail(),
            user.getOwnerCpf(),
            user.getBalance(),
            user.getAccountNumber(),
            user.getAgencyNumber(),
            user.getStatus(),
            currencies
        );
    }

    public BankUser save(BankUser user) {
        return bankUserRepository.save(user);
    }

    public boolean verifyPassword(Long userId, PasswordCheckDTO data) {
        
        BankUser user = getById(userId);
        
        boolean match = passwordEncoder.matches(data.password(), user.getPassword());

        return match;
    }
}
