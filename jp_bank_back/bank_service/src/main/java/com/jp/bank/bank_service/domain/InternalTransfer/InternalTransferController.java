package com.jp.bank.bank_service.domain.InternalTransfer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.bank.bank_service.domain.transactions.dto.CreateTransactionDTO;
import com.jp.bank.bank_service.domain.transactions.dto.TransactionResponseDTO;

@RestController
@RequestMapping("/internal-transfer")
public class InternalTransferController {

    private InternalTransferService internalTransferService;

    public InternalTransferController(InternalTransferService internalTransferService) {
        this.internalTransferService = internalTransferService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> transfer(@RequestHeader("Authorization") String authHeader, @RequestBody CreateTransactionDTO data) {
        TransactionResponseDTO response = internalTransferService.processInternalTransfer(authHeader, data);
        return ResponseEntity.ok(response);
    }
}
