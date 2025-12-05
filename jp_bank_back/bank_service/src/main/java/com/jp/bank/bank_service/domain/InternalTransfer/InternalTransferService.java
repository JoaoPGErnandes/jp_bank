package com.jp.bank.bank_service.domain.InternalTransfer;

import org.springframework.stereotype.Service;

import com.jp.bank.bank_service.auth.TokenService;
import com.jp.bank.bank_service.domain.transactions.TransactionService;
import com.jp.bank.bank_service.domain.transactions.dto.CreateTransactionDTO;
import com.jp.bank.bank_service.domain.transactions.dto.TransactionResponseDTO;
import com.jp.bank.bank_service.domain.user.BankUser;
import com.jp.bank.bank_service.domain.user.BankUserService;

import jakarta.transaction.Transactional;

@Service
public class InternalTransferService {

    private final BankUserService bankUserService;
    private final TransactionService transactionService;
    private final TokenService tokenService;

    public InternalTransferService(BankUserService bankUserService, TransactionService transactionService, TokenService tokenService) {
        this.bankUserService = bankUserService;
        this.transactionService = transactionService;
        this.tokenService = tokenService;
    }

    @Transactional
    public TransactionResponseDTO processInternalTransfer(String authHeader, CreateTransactionDTO data) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }
        
        BankUser sender = bankUserService.getById(userId);
        BankUser receiver = bankUserService.getByAgencyNumberAndAccountNumber(data.receiverAgencyNumber(), data.receiverAccountNumber());

        TransactionResponseDTO transaction = transactionService.createTransaction(userId, data);

        try {
            if(sender.getBalance().compareTo(data.amount()) < 0) {
                throw new RuntimeException("Saldo insuficiente");
            }

            sender.setBalance(sender.getBalance().subtract(data.amount()));

            transactionService.addReceiverMovement(transaction.transactionId(), data);

            receiver.setBalance(receiver.getBalance().add(data.amount()));

            transactionService.markAsSucccess(transaction.transactionId());

        } catch (Exception e) {
            transactionService.markAsFailed(transaction.transactionId(), e.getMessage());
            throw e;
        }

        return transactionService.getById(transaction.transactionId());
    }
}
