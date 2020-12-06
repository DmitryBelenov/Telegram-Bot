package net.bot;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class BotProperties {
    private static Logger log = Logger.getLogger(BotProperties.class);

    private BotProperties() {
    }

    static boolean initialize() {
        File propFile = new File(SysDataHolder.propertyPath + "_oskolnet_bot.properties");

        if (!propFile.exists()) {
            final File path = new File(SysDataHolder.propertyPath);
            boolean create = path.mkdirs();
            if (!create)
                log.error("Unable to create path " + SysDataHolder.propertyPath);

            log.error("Property file not found in " + SysDataHolder.propertyPath);
            return false;
        }

        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(propFile);
            properties.load(new InputStreamReader(input, Charset.forName("UTF-8")));
        } catch (IOException e) {
            log.error("Properties loading error\n" + e);
            return false;
        }

        SysDataHolder.botProps = properties;
        LoggerFormat.info("Properties load","OK");
        return true;
    }
}
