package net.bot;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BotProperties {
    private static Logger log = Logger.getLogger(BotProperties.class);
    public static final String propertyPath = System.getProperty("user.home") + "\\OskolNet\\props\\";

    private BotProperties() {
    }

    static boolean initialize() {
        File propFile = new File(propertyPath + "_oskolnet_bot.properties");

        if (!propFile.exists()) {
            final File path = new File(propertyPath);
            boolean create = path.mkdirs();
            if (!create)
                log.error("Filed create path " + propertyPath);

            log.error("Property file not found in " + propertyPath);
            return false;
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propFile.getAbsolutePath()));
        } catch (IOException e) {
            log.error("Properties loading error\n" + e);
            return false;
        }

        SysDataHolder.botProps = properties;
        LoggerFormat.info("Properties load","OK");
        return true;
    }
}
