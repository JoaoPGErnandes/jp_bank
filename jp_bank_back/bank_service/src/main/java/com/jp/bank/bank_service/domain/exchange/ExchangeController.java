package com.jp.bank.bank_service.domain.exchange;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.bank.bank_service.domain.exchange.dto.BuyCurrencyDTO;
import com.jp.bank.bank_service.domain.transactions.dto.TransactionResponseDTO;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/buy")
    public ResponseEntity<TransactionResponseDTO> buyCurrency(@RequestHeader("Authorization") String authHeader, @RequestBody BuyCurrencyDTO data) {
        TransactionResponseDTO result = exchangeService.buyCurrency(authHeader, data);
        return ResponseEntity.ok(result);
    }
}
