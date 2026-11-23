package net.zaydkassimi.customer_service;

import net.zaydkassimi.customer_service.entities.Customer;
import net.zaydkassimi.customer_service.service.CustomerService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(CustomerService customerService) {
        return args -> {
            List.of("Mohamed", "Iman", "Yassin").forEach(name -> {
                customerService.saveCustomer(Customer.builder()
                        .name(name)
                        .email(name.toLowerCase() + "@gmail.com")
                        .build());
            });
        };
    }

    @Bean
    public ToolCallbackProvider customerTools(CustomerService customerService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(customerService)
                .build();
    }
}