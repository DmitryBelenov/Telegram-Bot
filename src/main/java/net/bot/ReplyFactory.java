package net.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReplyFactory {
    private static Logger log = Logger.getLogger(ReplyFactory.class);
    private static Lock lock = new ReentrantLock();

    static Object getReply(BotApiObject update) {
        lock.lock();
        try {
            if (update instanceof Message) {
                Message msg = (Message) update;
                return getSendMessage(msg);
            } else if (update instanceof CallbackQuery) {
                CallbackQuery cbq = (CallbackQuery) update;
                return getSendMessage(cbq);
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    private static SendMessage getSendMessage(final Message msg) {
        SendMessage reply = new SendMessage();
        reply.setChatId(msg.getChatId());

        String r = reply(msg.getText(), reply);
        reply.setText(r);

        return reply;
    }

    private static SendMessage getSendMessage(CallbackQuery cbq) {
        SendMessage reply = new SendMessage();
        final Message msg = cbq.getMessage();
        reply.setChatId(msg.getChatId());

        final String data = cbq.getData();
        String r = reply(data, reply);
        reply.setText(r);

        return reply;
    }

    private static String reply(final String request, final SendMessage reply) {
        String response = "Текстовый Ответ";

        if (request.equals("info"))
            response = "Какая то информация";
        if (request.equals("menu"))
            response = "Какое то меню";
        if (request.equals("button")) {
            reply.setReplyMarkup(keyboard());
            response = "Кнопки";
        }
        return response;
    }


    private static ReplyKeyboard keyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("МЕНЮ")
                .setCallbackData("menu"));

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("ИНФОРМАЦИЯ")
                .setCallbackData("info"));

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
