package com.jp.bank.bank_service.domain.creditcard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jp.bank.bank_service.auth.TokenService;
import com.jp.bank.bank_service.domain.creditcard.dto.CreditCardPurchaseResponseDTO;
import com.jp.bank.bank_service.domain.user.BankUser;
import com.jp.bank.bank_service.domain.user.BankUserService;

@Service
public class CreditCardPurchaseService {

    private final CreditCardPurchaseRepository creditCardPurchaseRepository;
    private final TokenService tokenService;
    private final BankUserService bankUserService;

    public CreditCardPurchaseService(CreditCardPurchaseRepository creditCardPurchaseRepository, TokenService tokenService, BankUserService bankUserService) {
        this.creditCardPurchaseRepository = creditCardPurchaseRepository;
        this.tokenService = tokenService;
        this.bankUserService = bankUserService;
    }

    public void saveCreditCardPurchase(CreditCardPurchase purcahse) {
        creditCardPurchaseRepository.save(purcahse);
    }

    public List<CreditCardPurchaseResponseDTO> getPurchaseByUser(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }
        
        BankUser user = bankUserService.getById(userId);

        return creditCardPurchaseRepository.findByUser(user)
            .stream()
            .map(purchase -> new CreditCardPurchaseResponseDTO(
              purchase.getId(),
              purchase.getDescription(),
              purchase.getTotalAmount(),
              purchase.getTotalInstallments(),
              purchase.getPurchaseDate(),
              purchase.getCard().getNickname()  
            ))
            .toList();
    }
}
