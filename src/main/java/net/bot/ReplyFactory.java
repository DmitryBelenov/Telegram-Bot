package net.bot;

import net.bot.scenario.Scenario;
import net.bot.scenario.SuppDialogSc;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReplyFactory {
    // 😊 - smile 😃 - laugh 🍕 - pizza 📍 - location 📱 - iphone 📷 - camera 🌄 - mountains pic
    // 👤 - contact 🤖 - bot 🚫 - block it ❗️ - warning 👥 - contacts 🎥 - video cam 👍 - like

    private static final Set<Command> cmds = new HashSet<>();
    private static Logger log = Logger.getLogger(ReplyFactory.class);
    private static Lock lock = new ReentrantLock();

    static {
        cmds.addAll(Arrays.asList(Command.values()));
    }

    public enum Command {
        __menu(false, "МЕНЮ", null),
        __info(false, "О НАС", null),
        __tariff(false, "ТАРИФЫ", null),
        __supp(true, "ОПЕРАТОР", SuppDialogSc.class),

        __docs(false, "ДОКУМЕНТЫ", null),
        __contract(false, "ДОГОВОР", null),
        __requisites(false, "РЕКВИЗИТЫ", null),
        __license(false, "ЛИЦЕНЗИИ", null),
        __partners(false, "ПАРТНЕРЫ", null);

        private boolean hasScenario;
        private String ruName;
        private Class scClass;

        Command() {
        }

        Command(boolean hasScenario) {
            this.hasScenario = hasScenario;
        }

        Command(boolean hasScenario, String ruName) {
            this.hasScenario = hasScenario;
            this.ruName = ruName;
        }

        Command(boolean hasScenario, String ruName, Class scClass) {
            this.hasScenario = hasScenario;
            this.ruName = ruName;
            this.scClass = scClass;
        }

        public boolean isHasScenario() {
            return hasScenario;
        }

        public String getRuName() {
            return ruName;
        }

        public Scenario getScenario() {
            if (scClass == SuppDialogSc.class) {
                return new SuppDialogSc();
            }
            return null;
        }
    }

    static List<Object> reply(BotApiObject update) {
        lock.lock();
        try {
            if (update instanceof Message) {
                Message msg = (Message) update;
                return getReplyObject(msg);
            } else if (update instanceof CallbackQuery) {
                CallbackQuery cbq = (CallbackQuery) update;
                return getReplyObject(cbq);
            }
        } finally {
            lock.unlock();
        }
        log.error("No objects to reply ..");
        return Collections.emptyList();
    }

    private static List<Object> getReplyObject(final Message msg) {
        return reply(msg.getChatId(), msg.getText());
    }

    private static List<Object> getReplyObject(CallbackQuery cbq) {
        final Message msg = cbq.getMessage();
        return reply(msg.getChatId(), cbq.getData());
    }

    private static List<Object> reply(final Long chatId, final String income) {
        List<Object> reply;
        Command inCmd = null;
        if (income.startsWith("__")) {
            try {
                inCmd = Command.valueOf(income);
            } catch (IllegalArgumentException iae) {
                log.warn("User sent wrong command: '" + income + "'");
            }
        }
        if (StepByStepUserScenarios.scMap.keySet().contains(chatId)
                && Command.__menu != inCmd) {
            Scenario sc = StepByStepUserScenarios.scMap.get(chatId);
            reply = sc.getReplyType(chatId, income);
        } else if (inCmd != null && cmds.contains(inCmd)) {
            Command cmd = Command.valueOf(income);
            if (cmd.isHasScenario()) {
                Scenario sc = cmd.getScenario();
                StepByStepUserScenarios.scMap.put(chatId, sc);
                reply = sc.init(chatId);
            } else {
                reply = getNoScenarioBtnResponse(chatId, cmd);
            }
        } else {
            reply = aiResponse(chatId, income);
        }
        return reply;
    }

    private static List<Object> aiResponse(final Long chatId, final String income) {
        SendMessage aiResp = new SendMessage();
        aiResp.setChatId(chatId);

        String response = "Текстовый Ответ AI";
        if (income.toLowerCase().contains("привет")) {
            response = "Привет!";
        }
        aiResp.setText(response);
        aiResp.setReplyMarkup(KeyboardGenerator.fill(1, Command.__menu));
        return Collections.singletonList(aiResp);
    }

    private static List<Object> getNoScenarioBtnResponse(final Long chatId, final Command cmd) {

        if (cmd == Command.__info) {
            SendMessage info = new SendMessage();
            info.setChatId(chatId);
            info.setText(SysDataHolder.botProps.getProperty(cmd.name() + ".txt"));
            info.setReplyMarkup(KeyboardGenerator.fill(1, Command.__menu));
            return Collections.singletonList(info);

        }  else if (cmd == Command.__menu) {
            // clean user scenario if exists
            if (StepByStepUserScenarios.scMap.get(chatId) != null)
                StepByStepUserScenarios.scMap.remove(chatId);

            SendMessage menu = new SendMessage();
            menu.setChatId(chatId);
            menu.setText(SysDataHolder.botProps.getProperty(cmd.name() + ".txt"));
            menu.setReplyMarkup(KeyboardGenerator.fill(2,
                    Command.__info, Command.__tariff,
                    Command.__supp, Command.__docs));
            return Collections.singletonList(menu);
        }  else if (cmd == Command.__docs) {
            return Collections.singletonList(getDocMenuButtons(chatId));

        }  else if (cmd == Command.__license) {
            SendMessage loadMsg = new SendMessage();
            loadMsg.setChatId(chatId);
            loadMsg.setText("Загрузка лицензии ..");

            return getDocsFromPath(chatId, SysDataHolder.licensePath, loadMsg);
        }  else if (cmd == Command.__requisites) {
            SendMessage loadMsg = new SendMessage();
            loadMsg.setChatId(chatId);
            loadMsg.setText("Загрузка реквизитов ..");

            return getDocsFromPath(chatId, SysDataHolder.requisitesPath, loadMsg);
        }  else if (cmd == Command.__contract) {
            SendMessage loadMsg = new SendMessage();
            loadMsg.setChatId(chatId);
            loadMsg.setText("Загрузка договора ..");

            return getDocsFromPath(chatId, SysDataHolder.contractPath, loadMsg);
        }  else {
            return null;
        }
    }

    private static SendMessage getDocMenuButtons(final Long chatId) {
        SendMessage menu = new SendMessage();
        menu.setChatId(chatId);
        menu.setText(SysDataHolder.botProps.getProperty(Command.__docs.name() + ".txt"));
        menu.setReplyMarkup(KeyboardGenerator.fill(2,
                Command.__contract, Command.__requisites,
                Command.__license, Command.__partners,
                Command.__menu));
        return menu;
    }

    private static List<Object> getDocsFromPath(final Long chatId, final String path, final SendMessage loadMsg) {
        final File file = new File(path);
        final File[] files = file.listFiles();
        if (files != null && files.length != 0) {
            List<Object> licRespList = new ArrayList<>();

            SendDocument license = new SendDocument();
            license.setChatId(chatId);
            int i = 0;
            for (File f : files) {
                if (i > 0) break;
                license.setDocument(f);
                i++;
            }
            license.setReplyMarkup(KeyboardGenerator.fill(1, Command.__docs));

            if(loadMsg != null) licRespList.add(loadMsg);
            licRespList.add(license);

            return licRespList;
        } else {
            return Collections.singletonList(getDocMenuButtons(chatId));
        }
    }
}
