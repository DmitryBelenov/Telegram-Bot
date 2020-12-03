package net.bot;

import org.apache.log4j.Logger;

public class BotStarter {
    private static Logger log = Logger.getLogger(BotStarter.class);

    public static void main(String[] args) {
        BotGUI gui = new BotGUI();
        gui.start();
    }
}
