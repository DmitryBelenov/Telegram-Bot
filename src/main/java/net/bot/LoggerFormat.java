package net.bot;

import org.apache.log4j.Logger;

public class LoggerFormat {
    private static Logger log = Logger.getLogger(LoggerFormat.class);
    private static int cLen;

    static void info(final String infoText, final String state) {
        log.info(getFormatted(infoText, state));
    }

    public static void error(final String errorText, final String state) {
        log.error(getFormatted(errorText, state));
    }

    public static void warn(final String warnText, final String state) {
        log.warn(getFormatted(warnText, state));
    }

    private static String getFormatted(final String text, final String state) {
        StringBuilder infoLine = new StringBuilder();
        if (cLen == 0) {
            final String noState = text + " ............................... ";
            cLen = noState.length();
            infoLine.append(noState).append(state);
        } else {
            if (text.length() < cLen) {
                infoLine.append(text).append(' ');
                int i = 1;
                int add = cLen - infoLine.toString().length();
                while (i < add) {
                    infoLine.append('.');
                    i++;
                }
                if (i > 1)
                    infoLine.append(' ');

                infoLine.append(state);
            } else {
                infoLine.append(text).append(" .. ").append(state);
            }
        }

        return infoLine.toString();
    }
}
