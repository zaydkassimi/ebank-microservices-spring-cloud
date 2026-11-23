package net.zaydkassimi.ebank_service;

import net.zaydkassimi.ebank_service.entities.BankAccount;
import net.zaydkassimi.ebank_service.service.EBankService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class EbankServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(EBankService eBankService) {
        return args -> {
            for (int i = 1; i <= 3; i++) {
                for (int j = 1; j <= 5; j++) {
                    eBankService.save(BankAccount.builder()
                            .type(Math.random() > 0.5 ? "CURRENT_ACCOUNT" : "SAVINGS_ACCOUNT")
                            .balance(1000 + Math.random() * 60000)
                            .customerId((long) i)
                            .build());
                }
            }
        };
    }

    @Bean
    public ToolCallbackProvider ebankTools(EBankService eBankService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(eBankService)
                .build();
    }
}