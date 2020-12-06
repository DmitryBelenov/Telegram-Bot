package net.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardGenerator {
    private static final int btnInLineLimit = 5;

    public static synchronized ReplyKeyboard fill(int btnInLine, ReplyFactory.Command... btn) {
        if (btnInLine == 0 || btn == null || btn.length < btnInLine)
            return null;

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        int i = 0;
        int j = 0;
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        for (ReplyFactory.Command b : btn) {
            keyboardButtonsRow.add(new InlineKeyboardButton()
                    .setText(b.getRuName())
                    .setCallbackData(b.name()));
            i ++; j ++;
            if (i == btnInLine || i == btnInLineLimit || j == btn.length) {
                rowList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
                i = 0;
            }
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
