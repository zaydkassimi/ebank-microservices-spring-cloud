package net.zaydkassimi.ebank_service.service;

import net.zaydkassimi.ebank_service.clients.CustomerRestClient;
import net.zaydkassimi.ebank_service.entities.BankAccount;
import net.zaydkassimi.ebank_service.model.Customer;
import net.zaydkassimi.ebank_service.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EBankService {
    private final BankAccountRepository accountRepository;
    private final CustomerRestClient customerRestClient;

    public EBankService(BankAccountRepository accountRepository, CustomerRestClient customerRestClient) {
        this.accountRepository = accountRepository;
        this.customerRestClient = customerRestClient;
    }

    public List<BankAccount> getAllBankAccounts() {
        List<BankAccount> bankAccounts = accountRepository.findAll();
        bankAccounts.forEach(acc -> {
            acc.setCustomer(customerRestClient.getCustomerById(acc.getCustomerId()));
        });
        return bankAccounts;
    }

    public BankAccount getBankAccountById(String id) {
        BankAccount bankAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Customer customer = customerRestClient.getCustomerById(bankAccount.getCustomerId());
        bankAccount.setCustomer(customer);
        return bankAccount;
    }

    public BankAccount save(BankAccount bankAccount) {
        try {
            Customer customer = customerRestClient.getCustomerById(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreatedAt(new Date());
            bankAccount.setCustomer(customer);
            return accountRepository.save(bankAccount);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}