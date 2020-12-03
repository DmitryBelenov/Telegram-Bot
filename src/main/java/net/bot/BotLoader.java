package net.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotLoader {
    private static Logger log = Logger.getLogger(BotLoader.class);

    public BotLoader() {
    }

    static void load(JLabel label, JButton button) {
        if (!BotProperties.initialize()) {
            LoggerFormat.error("Properties loading", "FAULT");
            logLoadFault();
            label.setText(" Put properties to " + BotProperties.propertyPath);
            button.setText("Properties Error");
            button.setEnabled(false);
            return;
        }

        ApiContextInitializer.init();
        LoggerFormat.info("Api context initializing", "OK");

        TelegramBotsApi botsApi = new TelegramBotsApi();
        DefaultBotOptions botOptions = new DefaultBotOptions();

        try {
            botsApi.registerBot(new Bot(botOptions));
        } catch (TelegramApiRequestException t) {
            logLoadFault("Bot registration fault. " + t + "\n");
            label.setText(" Unable to register bot ");
            button.setText("Registration Error");
            button.setEnabled(false);
            return;
        }
        LoggerFormat.info("Bot registration", "OK");

        logLoadOK();

        while (BotGUI.working) {
            label.setText(" Bot working " + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy ").format(new Date()));
        }
    }

    private static void logLoadFault() {
        logLoadFault(null);
    }

    private static void logLoadFault(final String info) {
        if (info != null && info.length() > 0) {
            log.error(info);
        }

        log.error("********************************************************");
        log.error("|             SYSTEM FAILED TO START                   |");
        log.error("********************************************************\n");
    }

    private static void logLoadOK() {
        log.info("********************************************************");
        log.info("|                  SYSTEM LOADED                       |");
        log.info("********************************************************\n");
    }
}
