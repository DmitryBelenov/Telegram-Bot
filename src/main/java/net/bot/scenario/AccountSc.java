package net.bot.scenario;

import net.bot.KeyboardGenerator;
import net.bot.ReplyFactory;
import net.bot.StepByStepUserScenarios;
import net.bot.SysDataHolder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;

public class AccountSc implements Scenario {

    private static final String propPrefix = "account.srv";
    private int step = 1;

    public AccountSc() {
    }

    @Override
    public List<Object> init(Long chatId) {
        SendMessage firstMsg = new SendMessage();
        firstMsg.setChatId(chatId);
        firstMsg.setText(SysDataHolder.botProps.getProperty(propPrefix + step));
        return Collections.singletonList(firstMsg);
    }

    @Override
    public List<Object> getReplyType(Long chatId, String income) {
        // stub stub stub stub stub stub stub stub stub stub stub //
        if (StepByStepUserScenarios.scMap.get(chatId) != null)
            StepByStepUserScenarios.scMap.remove(chatId);

        SendMessage menu = new SendMessage();
        menu.setChatId(chatId);
        menu.setText("Личный кабинет временно не доступен");
        menu.setReplyMarkup(KeyboardGenerator.fillInLine(2,
                ReplyFactory.Command.__info, ReplyFactory.Command.__tariff,
                ReplyFactory.Command.__supp, ReplyFactory.Command.__docs,
                ReplyFactory.Command.__account));
        return Collections.singletonList(menu);
    }
}
