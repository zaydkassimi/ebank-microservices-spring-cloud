package net.zaydkassimi.ebank_bot.telegram;

import jakarta.annotation.PostConstruct;
import net.zaydkassimi.ebank_bot.agents.EBankAgent;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {

    @Value("${telegram.api.key}")
    private String telegramBotToken;

    private final EBankAgent eBankAgent;
    private TelegramClient telegramClient;

    public TelegramBot(EBankAgent eBankAgent) {
        this.eBankAgent = eBankAgent;
    }

    @PostConstruct
    public void initTelegramBot() {
        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(telegramBotToken, this);
            this.telegramClient = new OkHttpTelegramClient(telegramBotToken);
        } catch (Exception e) {
            System.err.println("Telegram Bot not started (check telegram.api.key): " + e.getMessage());
        }
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        String answer = eBankAgent.chat(new Prompt(messageText));
        sendTextMessage(chatId, answer);
    }

    private void sendTextMessage(long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}