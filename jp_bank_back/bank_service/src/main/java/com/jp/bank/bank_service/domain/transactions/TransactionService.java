package com.jp.bank.bank_service.domain.transactions;

//import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jp.bank.bank_service.auth.TokenService;
import com.jp.bank.bank_service.domain.transactions.dto.CreateTransactionDTO;
import com.jp.bank.bank_service.domain.transactions.dto.TransactionMovementDTO;
import com.jp.bank.bank_service.domain.transactions.dto.TransactionResponseDTO;
import com.jp.bank.bank_service.domain.transactions.dto.UserTransactionResponseDTO;
import com.jp.bank.bank_service.domain.user.BankUser;
import com.jp.bank.bank_service.domain.user.BankUserService;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankUserService bankUserService;
    private final TokenService tokenService;

    public TransactionService(TransactionRepository transactionRepository, BankUserService bankUserService, TokenService tokenService) {
        this.transactionRepository = transactionRepository;
        this.bankUserService = bankUserService;
        this.tokenService = tokenService;
    }

    @Transactional
    public TransactionResponseDTO createTransaction(long userId, CreateTransactionDTO dto) {
        BankUser sender = bankUserService.getById(userId);

        Transaction transaction = new Transaction();
        transaction.setType(dto.type());
        transaction.setStatus(TransactionStatus.PROCESSING);
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());

        if(dto.metadata() != null) {
            transaction.setMetadata(dto.metadata());
        }

        TransactionMovement transactionMovementSender = new TransactionMovement();
        transactionMovementSender.setTransaction(transaction);
        transactionMovementSender.setUser(sender);
        transactionMovementSender.setMovementType(MovementType.DEBIT);
        transactionMovementSender.setAmount(dto.amount());

        transaction.getMovements().add(transactionMovementSender);

        transactionRepository.save(transaction);

        return toDTO(transaction);
    }

    @Transactional
    public TransactionResponseDTO prepareToCreatetransaction(String authHeader, CreateTransactionDTO data) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        return this.createTransaction(userId, data);
    }

    @Transactional
    public void addReceiverMovement(Long transactionaId, CreateTransactionDTO data) {
        Transaction transaction = transactionRepository.findById(transactionaId).orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.PROCESSING) {
            throw new RuntimeException("Cannot add receiver movement unless PROCESSING");
        }

        if(data.receiverAgencyNumber() == null || data.receiverAccountNumber() == null) {
            return;
        }

        BankUser receiver = bankUserService.getByAgencyNumberAndAccountNumber(data.receiverAgencyNumber(), data.receiverAccountNumber());

        TransactionMovement transactionMovementReceiver = new TransactionMovement();
        transactionMovementReceiver.setTransaction(transaction);
        transactionMovementReceiver.setUser(receiver);
        transactionMovementReceiver.setMovementType(MovementType.CREDIT);
        transactionMovementReceiver.setAmount(data.amount());

        transaction.getMovements().add(transactionMovementReceiver);
        transactionRepository.save(transaction);
    }

    public TransactionResponseDTO getById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
        return toDTO(transaction);
    }

    public List<UserTransactionResponseDTO> getByUser(Long userId) {
        return transactionRepository.findByMovementsUserBankId(userId).stream().map(transaction -> toUserTransactionDTO(transaction, userId)).collect(Collectors.toList());
    }

    private UserTransactionResponseDTO toUserTransactionDTO(Transaction transaction, Long userId) {

        TransactionMovement userMovement = transaction.getMovements()
            .stream()
            .filter(m -> m.getUser().getBankId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Movimento do usuário não encontrado na transação"));

        return new UserTransactionResponseDTO(
            transaction.getTransactionId(),
            transaction.getType(),
            transaction.getStatus(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getCreatedAt(),
            userMovement.getTransactionMovementId(),
            userMovement.getMovementType()
        );
    }

    private TransactionResponseDTO toDTO(Transaction transaction) {
        List<TransactionMovementDTO> movements = transaction.getMovements().stream()
            .map(mov -> new TransactionMovementDTO(
                mov.getTransactionMovementId(),
                mov.getUser().getBankId(),
                mov.getMovementType(),
                mov.getAmount(),
                mov.getTimestamp()
            )).toList();

            return new TransactionResponseDTO(
                transaction.getTransactionId(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                movements
            );
    }

    @Transactional
    public void markAsSucccess(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void markAsFailed(Long transactionId, String reason) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setDescription(reason);
        transactionRepository.save(transaction);
    }

    public Transaction returnTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}
