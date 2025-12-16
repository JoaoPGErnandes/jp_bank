package com.jp.bank.bank_service.domain.creditcard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jp.bank.bank_service.auth.TokenService;
import com.jp.bank.bank_service.domain.creditcard.dto.CreateCreditCardTransactionDTO;
import com.jp.bank.bank_service.domain.transactions.metadata.CreditCardMetadata;
import com.jp.bank.bank_service.domain.user.BankUser;
import com.jp.bank.bank_service.domain.user.BankUserService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
public class CreditCardTransactionService {

    private final CreditCardTransactionRepository creditCardTransactionRepository;
    private final CreditCardService creditCardService;
    private final BankUserService bankUserService;
    private final CreditCardPurchaseService creditCardPurchaseService;
    private final TokenService tokenService;

    public CreditCardTransactionService(CreditCardTransactionRepository creditCardTransactionRepository, CreditCardService creditCardService, BankUserService bankUserService, CreditCardPurchaseService creditCardPurchaseService, TokenService tokenService) {
        this.creditCardService = creditCardService;
        this.creditCardTransactionRepository = creditCardTransactionRepository;
        this.bankUserService = bankUserService;
        this.creditCardPurchaseService = creditCardPurchaseService;
        this.tokenService = tokenService;
    }

    @Transactional
    public void createCreditCardTransaction(CreateCreditCardTransactionDTO data) {

        CreditCard card = creditCardService.validateCard(data.cardNumber(), data.cvv(), data.cardExpiry());

        BankUser user = card.getOwner();

        BigDecimal totalAmount = data.installmentAmount().multiply(BigDecimal.valueOf(data.totalInstallments()));

        if(user.getCreditLimit().subtract(user.getCreditLimitUsed()).compareTo(totalAmount) < 0) {
            throw new RuntimeException("Crédito insuficiente");
        }

        LocalDate purchaseDate = LocalDate.now();
        int purchaseDay = purchaseDate.getDayOfMonth();

        CreditCardPurchase creditPurchase = new CreditCardPurchase();
        creditPurchase.setCard(card);
        creditPurchase.setUser(user);
        creditPurchase.setTotalAmount(totalAmount);
        creditPurchase.setTotalInstallments(data.totalInstallments());
        creditPurchase.setPurchaseDate(purchaseDate);
        creditPurchase.setDescription(data.description());
        creditPurchase.setMetadata(null);

        for(int i = 1; i <= data.totalInstallments(); i++) {
            
            LocalDate baseMonth = purchaseDate.plusMonths(i-1);

            LocalDate dueDate = baseMonth.withDayOfMonth(Math.min(purchaseDay, baseMonth.lengthOfMonth())); //LocalDate.now().plusMonths(i-1);

            CreditCardMetadata metadata = new CreditCardMetadata(
                card.getId(),
                data.totalInstallments(),
                0.0,
                totalAmount,
                data.installmentAmount(),
                dueDate
            );
            String metadataJson;
            try {
                metadataJson = new ObjectMapper().writeValueAsString(metadata);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao gerar metadatas de transação de crédito", e);
            }

            CreditCardTransaction creditTransaction = new CreditCardTransaction();
            creditTransaction.setCard(card);
            creditTransaction.setUser(user);
            creditTransaction.setInvoice(null);
            creditTransaction.setInstallmentAmount(data.installmentAmount());
            creditTransaction.setInstallmentNumber(i);
            creditTransaction.setTotalInstallments(data.totalInstallments());
            creditTransaction.setDueDate(dueDate);
            creditTransaction.setDescription(data.description());
            creditTransaction.setMetadata(metadataJson);
            creditTransaction.setPurchase(creditPurchase);

            creditPurchase.getInstallments().add(creditTransaction);
        }

        bankUserService.addBankUserCreditLimitUsed(user, totalAmount);

        creditCardPurchaseService.saveCreditCardPurchase(creditPurchase);
    }

    public List<CreditCardTransaction> findInstallmentsWithoutInvoice(Long cardId, LocalDate start, LocalDate end) {
        return creditCardTransactionRepository.findInstallmentWithoutInvoice(cardId, start, end);
    }

    public BigDecimal getCurrentInvoiceAmount(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        BankUser user = bankUserService.getById(userId);

        LocalDate today = LocalDate.now();
        int closingDay = user.getCreditCardClosingDay();

        LocalDate lastClosingDate = today.withDayOfMonth(closingDay);
        if (today.getDayOfMonth() < closingDay) {
            lastClosingDate = lastClosingDate.minusMonths(1);
        }

        LocalDate nextClosinDate = lastClosingDate.plusMonths(1);

        return creditCardTransactionRepository.sumInvoiceAmount(user, lastClosingDate, nextClosinDate);
    }
}
