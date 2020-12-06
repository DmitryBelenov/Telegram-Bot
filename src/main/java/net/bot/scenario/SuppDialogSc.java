package net.bot.scenario;

import net.bot.KeyboardGenerator;
import net.bot.ReplyFactory;
import net.bot.StepByStepUserScenarios;
import net.bot.SysDataHolder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuppDialogSc implements Scenario {

    private static final Pattern phoneRegexp = Pattern.compile("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");
    private static final String propPrefix = "supp.srv";
    private int step = 1;

    public SuppDialogSc() {
    }

    @Override
    public List<Object> init(final Long chatId) {
        SendMessage firstMsg = new SendMessage();
        firstMsg.setChatId(chatId);
        firstMsg.setText(SysDataHolder.botProps.getProperty(propPrefix + step));
        return Collections.singletonList(firstMsg);
    }

    @Override
    public List<Object> getReplyType(Long chatId, String income) {
        SendMessage stepReply = new SendMessage();
        stepReply.setChatId(chatId);
        // filter button callbacks
        if (btnCallbackChecked(income)) {
            switch (step) {
                case 1:
                    // check user text here

                    step++;
                    stepReply.setText(SysDataHolder.botProps.getProperty(propPrefix + step));
                    StepByStepUserScenarios.scMap.put(chatId, this);
                    break;
                case 2:
                    // check user phone, if OK send request to operator
                    final Matcher phoneMatcher = phoneRegexp.matcher(income);
                    if (phoneMatcher.matches()) {
                        step++;
                        stepReply.setText(SysDataHolder.botProps.getProperty(propPrefix + step) + " \uD83D\uDE0A");
                        stepReply.setReplyMarkup(KeyboardGenerator.fill(1, ReplyFactory.Command.__menu));

                        StepByStepUserScenarios.scMap.remove(chatId);
                    } else
                        stepReply.setText("Укажите, пожалуйста, телефон корректно\nНапример: 9119203344 или 55-77-11");

                    break;
                default:
                    break;
            }
        } else
            stepReply.setText("Вы не завершили действие: " + SysDataHolder.botProps.getProperty(propPrefix + step).toLowerCase());

        return Collections.singletonList(stepReply);
    }
}
