package net.zaydkassimi.ebank_bot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class EBankAgent {

    private final ChatClient chatClient;

    public EBankAgent(ChatClient.Builder builder, ChatMemory chatMemory, ToolCallbackProvider tools) {
        this.chatClient = builder
                .defaultSystem("""
                        Vous êtes un assistant bancaire qui se charge de répondre aux questions de l'utilisateur
                        en fonction du contexte fourni à propos des clients et des comptes bancaires.
                        Si aucun contexte n'est fourni ou si la question est hors sujet, répondez par : Je ne sais pas.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(tools)
                .build();
    }

    public String chat(Prompt prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}