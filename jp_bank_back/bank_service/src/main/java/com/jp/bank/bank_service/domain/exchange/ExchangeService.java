package com.jp.bank.bank_service.domain.exchange;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.jp.bank.bank_service.auth.TokenService;
import com.jp.bank.bank_service.domain.exchange.dto.BuyCurrencyDTO;
import com.jp.bank.bank_service.domain.transactions.TransactionService;
import com.jp.bank.bank_service.domain.transactions.TransactionType;
import com.jp.bank.bank_service.domain.transactions.dto.CreateTransactionDTO;
import com.jp.bank.bank_service.domain.transactions.dto.TransactionResponseDTO;
import com.jp.bank.bank_service.domain.transactions.metadata.ExchangeMetadata;
import com.jp.bank.bank_service.domain.user.BankUser;
import com.jp.bank.bank_service.domain.user.BankUserCurrencyBalance;
import com.jp.bank.bank_service.domain.user.BankUserCurrencyBalanceService;
import com.jp.bank.bank_service.domain.user.BankUserService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
public class ExchangeService {

    private final BankUserService bankUserService;
    private final BankUserCurrencyBalanceService bankUserCurrencyBalanceService;
    private final TransactionService transactionService;
    private final TokenService tokenService;

    public ExchangeService(BankUserService bankUserService, BankUserCurrencyBalanceService bankUserCurrencyBalanceService, TransactionService transactionService, TokenService tokenService) {
        this.bankUserService = bankUserService;
        this.bankUserCurrencyBalanceService = bankUserCurrencyBalanceService;
        this.transactionService = transactionService;
        this.tokenService = tokenService;
    }

    @Transactional
    public TransactionResponseDTO buyCurrency(String authHeader, BuyCurrencyDTO data) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }
        
        BankUser user = bankUserService.getById(userId);


        BigDecimal totalCost = data.priceInBRL().multiply(data.amount());

        String description = "Compra de " + data.amount() + " " + data.currency() + " ao valor de " + data.priceInBRL();

        ExchangeMetadata metadata = new ExchangeMetadata(data.currency(), data.amount(), data.priceInBRL());
        String metadataJson;
        try {
            metadataJson = new ObjectMapper().writeValueAsString(metadata);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar metadatas da transação", e);
        }

        CreateTransactionDTO transactionData = new CreateTransactionDTO(
            TransactionType.EXCHANGE,
            data.amount().multiply(data.priceInBRL()),
            description,
            null,
            null,
            metadataJson
        );

        TransactionResponseDTO transaction = transactionService.createTransaction(userId, transactionData);

        

        try {
            if (user.getBalance().compareTo(totalCost) < 0) {
                throw new RuntimeException("Saldo insuficiente");
            }

            user.setBalance(user.getBalance().subtract(totalCost));
            bankUserService.save(user);

            BankUserCurrencyBalance currency = bankUserCurrencyBalanceService.getByUserAndCurrency(user, data.currency())
                                                                        .orElseGet(() -> new BankUserCurrencyBalance(user, data.currency(), BigDecimal.ZERO));
            
            currency.setBalance(currency.getBalance().add(data.amount()));
            bankUserCurrencyBalanceService.save(currency);

            transactionService.markAsSucccess(transaction.transactionId());

        } catch (Exception e) {
            transactionService.markAsFailed(transaction.transactionId(), e.getMessage());
            throw e;
        }

        return transactionService.getById(transaction.transactionId());
    }
}
