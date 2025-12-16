package com.jp.bank.bank_service.domain.creditcard.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jp.bank.bank_service.auth.TokenService;
import com.jp.bank.bank_service.domain.creditcard.CreditCardTransaction;
import com.jp.bank.bank_service.domain.creditcard.CreditCardTransactionService;
import com.jp.bank.bank_service.domain.creditcard.invoice.dto.CreditCardInvoiceResponseDTO;
import com.jp.bank.bank_service.domain.creditcard.invoice.dto.InvoicePurchaseDTO;
import com.jp.bank.bank_service.domain.creditcard.invoice.dto.PaymentInfoDTO;
import com.jp.bank.bank_service.domain.transactions.Transaction;
import com.jp.bank.bank_service.domain.transactions.TransactionService;
import com.jp.bank.bank_service.domain.transactions.TransactionType;
import com.jp.bank.bank_service.domain.transactions.dto.CreateTransactionDTO;
import com.jp.bank.bank_service.domain.transactions.dto.TransactionResponseDTO;
import com.jp.bank.bank_service.domain.transactions.metadata.CreditCardInvoiceMetadata;
import com.jp.bank.bank_service.domain.user.BankUser;
import com.jp.bank.bank_service.domain.user.BankUserService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
public class CreditCardInvoiceService {

    private final CreditCardInvoiceRepository creditCardInvoiceRepository;
    private final CreditCardTransactionService creditCardTransactionService;
    private final TransactionService transactionService;
    private final BankUserService bankUserService;
    private final TokenService tokenService;

    public CreditCardInvoiceService(CreditCardInvoiceRepository creditCardInvoiceRepository, CreditCardTransactionService creditCardTransactionService, TransactionService transactionService, BankUserService bankUserService, TokenService tokenService) {
        this.creditCardInvoiceRepository = creditCardInvoiceRepository;
        this.creditCardTransactionService = creditCardTransactionService;
        this.transactionService = transactionService;
        this.bankUserService = bankUserService;
        this.tokenService = tokenService;
    }

    public CreditCardInvoice generateInvoice(BankUser user, LocalDate closingDate) {

        LocalDate startPeriod = closingDate.minusMonths(1).plusDays(1);
        LocalDate endPeriod = closingDate;

        List<CreditCardTransaction> installments = creditCardTransactionService.findInstallmentsWithoutInvoice(user.getBankId(), startPeriod, endPeriod);

        if (installments.isEmpty()) return null;

        CreditCardInvoice invoice = new CreditCardInvoice();
        invoice.setUser(user);
        invoice.setClosingDate(closingDate);
        invoice.setDueDate(closingDate.plusDays(7));
        invoice.setPaid(false);

        BigDecimal total = BigDecimal.ZERO;

        for (CreditCardTransaction inst : installments) {
            inst.setInvoice(invoice);
            invoice.getInstallments().add(inst);
            total = total.add(inst.getInstallmentAmount());
        }

        invoice.setTotalAmount(total);

        return creditCardInvoiceRepository.save(invoice);
    }

    @Transactional
    public TransactionResponseDTO payInvoice(String authHeader, Long invoiceId) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }
        
        BankUser authenticatedUser = bankUserService.getById(userId);
        
        CreditCardInvoice invoice = creditCardInvoiceRepository.findById(invoiceId).orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.isPaid()) {
            throw new RuntimeException("Invoice already paid");
        }

        BankUser user = invoice.getUser();

        if (!authenticatedUser.getBankId().equals(user.getBankId())) {
            throw new RuntimeException("Você não tem permissão para pagar essa fatura");
        }

        String description = "Pagamento da fatura do cartão";

        CreditCardInvoiceMetadata metadata = new CreditCardInvoiceMetadata(invoice.getId());
        String metadataJson;
        try {
            metadataJson = new ObjectMapper().writeValueAsString(metadata);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar metadatas da transação", e);
        }

        CreateTransactionDTO transactionData = new CreateTransactionDTO(
            TransactionType.CARD_PAYMENT,
            invoice.getTotalAmount(),
            description,
            null,
            null,
            metadataJson
        );

        TransactionResponseDTO payment = transactionService.createTransaction(user.getBankId(), transactionData);
        Transaction transaction = transactionService.returnTransactionById(payment.transactionId());

        try {
            if (user.getBalance().compareTo(invoice.getTotalAmount()) < 0) {
                throw new RuntimeException("Saldo insuficiente");
            }

            user.setBalance(user.getBalance().subtract(invoice.getTotalAmount()));
            bankUserService.save(user);

            bankUserService.subtractbankUserCreditLimitused(user, invoice.getTotalAmount());

            invoice.setPaid(true);
            invoice.setPaymentTransaction(transaction);
            creditCardInvoiceRepository.save(invoice);

            transactionService.markAsSucccess(payment.transactionId());

        } catch (Exception e) {
            transactionService.markAsFailed(payment.transactionId(), e.getMessage());
            throw e;
        }

        return transactionService.getById(payment.transactionId());
    }


    public CreditCardInvoiceResponseDTO getInvoice(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }
        
        BankUser user = bankUserService.getById(userId);

        CreditCardInvoice invoice = creditCardInvoiceRepository.findByUserAndPaidFalse(user).get(); //orElseThrow(() -> new RuntimeException("Nenhuma fatura encontrada"));

        List<InvoicePurchaseDTO> purchases = invoice.getInstallments()
            .stream()
            .map(tx -> new InvoicePurchaseDTO(
                tx.getPurchase().getId(),
                tx.getDescription(),
                tx.getInstallmentAmount(),
                tx.getInstallmentNumber(),
                tx.getTotalInstallments()
            ))
            .toList();

        PaymentInfoDTO payment = invoice.isPaid() ? new PaymentInfoDTO(
            invoice.getPaymentTransaction().getTransactionId(),
            invoice.getTotalAmount(),
            invoice.getPaymentTransaction().getCreatedAt()
        ) : null;

        return new CreditCardInvoiceResponseDTO(
            invoice.getId(),
            invoice.getClosingDate(),
            invoice.getDueDate(),
            invoice.getTotalAmount(),
            invoice.isPaid(),
            payment,
            purchases
        );
    }
}
