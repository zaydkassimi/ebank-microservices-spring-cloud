package net.zaydkassimi.ebank_bot.discord;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.zaydkassimi.ebank_bot.agents.EBankAgent;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DiscordBot extends ListenerAdapter {

    @Value("${discord.api.key}")
    private String discordBotToken;

    private final EBankAgent eBankAgent;

    public DiscordBot(EBankAgent eBankAgent) {
        this.eBankAgent = eBankAgent;
    }

    @PostConstruct
    public void initDiscordBot() {
        try {
            JDABuilder.createDefault(discordBotToken)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(this)
                    .build();
        } catch (Exception e) {
            System.err.println("Discord Bot not started (check discord.api.key): " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String messageText = event.getMessage().getContentRaw();
        String answer = eBankAgent.chat(new Prompt(messageText));
        event.getChannel().sendMessage(answer).queue();
    }
}