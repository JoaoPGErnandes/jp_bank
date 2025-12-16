package com.jp.bank.bank_service.scheduller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.jp.bank.bank_service.domain.creditcard.invoice.CreditCardInvoiceService;
import com.jp.bank.bank_service.domain.user.BankUser;
import com.jp.bank.bank_service.domain.user.BankUserService;

@Component
public class InvoiceScheduler {

    private final BankUserService bankUserService;
    private final CreditCardInvoiceService creditCardInvoiceService;

    public InvoiceScheduler(BankUserService bankUserService, CreditCardInvoiceService creditCardInvoiceService) {
        this.bankUserService = bankUserService;
        this.creditCardInvoiceService = creditCardInvoiceService;
    }

    @Scheduled(cron = "0 27 15 * * *", zone = "America/Sao_Paulo")
    public void generateInvoicesDaily() {
        List<BankUser> users = bankUserService.findAllUsersWithCreditCard();

        LocalDate today = LocalDate.now();

        for (BankUser user : users) {
            if (user.getCreditCardClosingDay() == null) continue;
            if (user.getCreditCardClosingDay() == today.getDayOfMonth()) {
                creditCardInvoiceService.generateInvoice(user, today);
            }
        }
    }
}
