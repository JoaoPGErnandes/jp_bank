package com.jp.bank.bank_service.domain.creditcard;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.bank.bank_service.domain.creditcard.dto.CreateCreditCardDTO;
import com.jp.bank.bank_service.domain.creditcard.dto.CreateCreditCardTransactionDTO;
import com.jp.bank.bank_service.domain.creditcard.dto.CreditCardPurchaseResponseDTO;
import com.jp.bank.bank_service.domain.creditcard.dto.CreditCardResponseDTO;
import com.jp.bank.bank_service.domain.creditcard.dto.CreditCardShowInfoDTO;
import com.jp.bank.bank_service.domain.creditcard.invoice.CreditCardInvoiceService;
import com.jp.bank.bank_service.domain.creditcard.invoice.dto.CreditCardInvoiceResponseDTO;

@RestController
@RequestMapping("creditcard")
public class CreditCardController {

    private final CreditCardService creditCardService;
    private final CreditCardTransactionService creditCardTransactionService;
    private final CreditCardPurchaseService creditCardPurchaseService;
    private final CreditCardInvoiceService creditCardInvoiceService;

    public CreditCardController(CreditCardService creditCardService, CreditCardTransactionService creditCardTransactionService, CreditCardPurchaseService creditCardPurchaseService, CreditCardInvoiceService creditCardInvoiceService) {
        this.creditCardService = creditCardService;
        this.creditCardTransactionService = creditCardTransactionService;
        this.creditCardPurchaseService = creditCardPurchaseService;
        this.creditCardInvoiceService = creditCardInvoiceService;
    }

    @PostMapping("/createcard")
    public ResponseEntity<?> createCard(@RequestHeader("Authorization") String authHeader, @RequestBody CreateCreditCardDTO data) {
        CreditCardResponseDTO response = creditCardService.createCard(authHeader, data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyWirhCreditCard(@RequestBody CreateCreditCardTransactionDTO data) {
        creditCardTransactionService.createCreditCardTransaction(data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usercards")
    public ResponseEntity<List<CreditCardShowInfoDTO>> getCardsByUser(@RequestHeader("Authorization") String authHeader) {
        List<CreditCardShowInfoDTO> cards = creditCardService.getCardsByUser(authHeader);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/invoiceNotClosed")
    public ResponseEntity<BigDecimal> getThisMonthInvoceSpends(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(creditCardTransactionService.getCurrentInvoiceAmount(authHeader));
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<CreditCardPurchaseResponseDTO>> getPurchases(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(creditCardPurchaseService.getPurchaseByUser(authHeader));
    }

    @GetMapping("/invoice")
    public ResponseEntity<CreditCardInvoiceResponseDTO> getInvoice(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(creditCardInvoiceService.getInvoice(authHeader));
    }
}
