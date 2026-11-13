package net.zaydkassimi.ebank_service.web;

import net.zaydkassimi.ebank_service.entities.BankAccount;
import net.zaydkassimi.ebank_service.service.EBankService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountRestController {
    private final EBankService eBankService;

    public AccountRestController(EBankService eBankService) {
        this.eBankService = eBankService;
    }

    @GetMapping("/accounts")
    public List<BankAccount> getAllBankAccounts() {
        return eBankService.getAllBankAccounts();
    }

    @GetMapping("/accounts/{id}")
    public BankAccount getBankAccountById(@PathVariable String id) {
        return eBankService.getBankAccountById(id);
    }

    @PostMapping("/accounts")
    public BankAccount saveAccount(@RequestBody BankAccount bankAccount) {
        return eBankService.save(bankAccount);
    }
}