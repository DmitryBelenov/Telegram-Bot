package net.bot.scenario;

import java.util.List;

public interface Scenario {
    List<Object> init(final Long chatId);
    List<Object> getReplyType(final Long chatId, final String income);

    default boolean btnCallbackChecked(final String income) {
        return !income.startsWith("__");
    }
}
