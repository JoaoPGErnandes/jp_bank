package com.jp.bank.bank_service.domain.transactions;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.bank.bank_service.domain.transactions.dto.CreateTransactionDTO;
import com.jp.bank.bank_service.domain.transactions.dto.TransactionResponseDTO;
import com.jp.bank.bank_service.domain.transactions.dto.UserTransactionResponseDTO;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@RequestHeader("Authorization") String authHeader, @RequestBody CreateTransactionDTO dto) {
        return ResponseEntity.ok(transactionService.prepareToCreatetransaction(authHeader, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserTransactionResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getByUser(userId));
    }
}
