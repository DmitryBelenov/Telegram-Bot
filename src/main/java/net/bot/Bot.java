package net.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Set;

public class Bot extends TelegramLongPollingBot {
    private static Logger log = Logger.getLogger(Bot.class);

    Bot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message m = update.getMessage();

            cleanBefore(m.getChatId());
            handleMsg(m);
        } else if (update.hasCallbackQuery()) {
            CallbackQuery cbq = update.getCallbackQuery();
            Message m = cbq.getMessage();

            cleanBefore(m.getChatId());
            handleCbQuery(cbq);
        }
    }

    private void cleanBefore(final Long chatId) {
        Set<DeleteMessage> cleanSet = UserChatStackCleaner.getOldMsgToDelete(chatId);
        if (!cleanSet.isEmpty()) {
            cleanSet.forEach(deleteMessage -> {
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    log.warn("Unable to delete Message " + deleteMessage.getMessageId() + "\n" + e);
                }
            });
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
                    SendMessage sm = (SendMessage) r;
                    Message m = execute(sm);

                    UserChatStackCleaner.addToStackMap(sm.getChatId(), m.getMessageId());
                } else if (r instanceof SendDocument) {
                    SendDocument sd = (SendDocument) r;

                    Message m = execute(sd);
                    UserChatStackCleaner.addToStackMap(sd.getChatId(), m.getMessageId());
                }
            }
        } catch (TelegramApiException t) {
            log.error("Send reply error." + t);
        }
    }
}
