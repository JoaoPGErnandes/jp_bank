package com.jp.bank.bank_service.domain.creditcard;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jp.bank.bank_service.auth.TokenService;
import com.jp.bank.bank_service.domain.creditcard.dto.CreateCreditCardDTO;
import com.jp.bank.bank_service.domain.creditcard.dto.CreditCardResponseDTO;
import com.jp.bank.bank_service.domain.creditcard.dto.CreditCardShowInfoDTO;
import com.jp.bank.bank_service.domain.user.BankUser;
import com.jp.bank.bank_service.domain.user.BankUserService;

import jakarta.transaction.Transactional;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final TokenService tokenService;
    private final BankUserService bankUserService;

    public CreditCardService(CreditCardRepository creditCardRepository, TokenService tokenService, BankUserService bankUserService) {
        this.creditCardRepository = creditCardRepository;
        this.tokenService = tokenService;
        this.bankUserService = bankUserService;
    }

    @Transactional
    public CreditCardResponseDTO createCard(String authHeader, CreateCreditCardDTO data) {   
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }
        
        BankUser owner = bankUserService.getById(userId);

        if (owner.getCreditCardClosingDay() == null) {
            bankUserService.setBankUserCreditCardClosingDay(owner, data.closingDay());
        }

        bankUserService.generateBankUserCreditLimit(owner);

        CreditCard card = new CreditCard();

        card.setOwner(owner);
        card.setVirtual(data.virtual());
        card.setNickname(data.nickname());

        card.setCardNumber(generateUniqueCardNumber());
        card.setCvv(generateCVV());
        card.setExpiry(generateExpiryDate());
        card.setClosingDay(owner.getCreditCardClosingDay());

        CreditCard savedCard = creditCardRepository.save(card);
        
        return toResponse(savedCard);
    }

    public String generateUniqueCardNumber() {
        while (true) {
            String cardNumber = generateCardNumber();
            if(!creditCardRepository.findByCardNumber(cardNumber).isPresent()) {
                return cardNumber;
            }
        }
    }

    private String generateCardNumber() {
        String bin = "412345";

        Random random = new Random();

        StringBuilder number = new StringBuilder(bin);
        for(int i = 0; i < 9; i++) {
            number.append(random.nextInt(10));
        }

        int checkDigit = calculateLuhnCheckDigit(number.toString());
        number.append(checkDigit);

        return number.toString();
    }

    private int calculateLuhnCheckDigit(String number) {

        int sum = 0;
        boolean alternate = true;
        
        for(int i = number.length() -  1; i > 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));

            if (alternate) {
                n *= 2;
                if(n > 0) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }

    private String generateCVV() {
        Random random = new Random();
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    private LocalDate generateExpiryDate() {
        Random random = new Random();
        int yearsToAdd = 4 + random.nextInt(3);

        int month = 1 + random.nextInt(12);

        return LocalDate.now().plusYears(yearsToAdd).withMonth(month);
    }

    private CreditCardResponseDTO toResponse(CreditCard card) {
        return new CreditCardResponseDTO(
            card.getId(),
            card.getOwner().getBankId(),
            card.getCardNumber(),
            card.getCvv(),
            card.getExpiry(),
            card.isVirtual(),
            card.getNickname(),
            card.getClosingDay(),
            card.isActive()
        );
    }

    public CreditCard validateCard(String cardNumber, String cvv, LocalDate expiry) {
        return creditCardRepository.findByCardNumberAndCvvAndExpiry(cardNumber, cvv, expiry).orElseThrow(() -> new RuntimeException("Invalid card data"));
    }

    public List<CreditCardShowInfoDTO> getCardsByUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        BankUser owner = bankUserService.getById(userId);

        List<CreditCard> creditCards = creditCardRepository.findByOwner(owner).get();

        return creditCards.stream()
            .map(card -> new CreditCardShowInfoDTO(
                card.getCardNumber(),
                card.getCvv(),
                card.getExpiry(),
                card.getNickname()
            ))
            .collect(Collectors.toList());
    }

}
