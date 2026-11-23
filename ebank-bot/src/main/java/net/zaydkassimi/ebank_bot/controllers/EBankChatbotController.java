package net.zaydkassimi.ebank_bot.controllers;

import net.zaydkassimi.ebank_bot.agents.EBankAgent;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EBankChatbotController {

    private final EBankAgent eBankAgent;

    public EBankChatbotController(EBankAgent eBankAgent) {
        this.eBankAgent = eBankAgent;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam(name = "query", defaultValue = "Bonjour") String query) {
        return eBankAgent.chat(new Prompt(query));
    }
}