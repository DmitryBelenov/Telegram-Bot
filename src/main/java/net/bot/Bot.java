package net.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private static Logger log = Logger.getLogger(Bot.class);

    Bot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleMsg(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCbQuery(update.getCallbackQuery());
        }
    }

    @Override
    public String getBotUsername() {
        return SysDataHolder.botProps.getProperty("bot.name");
    }

    @Override
    public String getBotToken() {
        return SysDataHolder.botProps.getProperty("bot.token");
    }

    private void handleMsg(final Message msg) {
        List<Object> o = ReplyFactory.reply(msg);
        send(o);
    }

    private void handleCbQuery(final CallbackQuery cbq) {
        List<Object> o = ReplyFactory.reply(cbq);
        send(o);
    }

    private void send(List<Object> respList) {
        try {
            for (Object r : respList) {
                if (r instanceof SendMessage) {
                    execute((SendMessage) r);
                } else if (r instanceof SendDocument) {
                    execute((SendDocument) r);
                }
            }
        } catch (TelegramApiException t) {
            log.error("Failed to send reply." + t);
        }
    }
}
