package net.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserChatStackCleaner {
    private static Logger log = Logger.getLogger(UserChatStackCleaner.class);
    private static Map<Long, Set<Integer>> stackMap = new ConcurrentHashMap<>();

    static synchronized Set<DeleteMessage> getOldMsgToDelete(final Long chatId) {
        Set<DeleteMessage> toDelete = new HashSet<>();
        if (stackMap.keySet().contains(chatId)) {
            Set<Integer> msgIds = stackMap.get(chatId);
            msgIds.forEach(id -> {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId);
                deleteMessage.setMessageId(id);
                toDelete.add(deleteMessage);
            });

            stackMap.remove(chatId);
        }

        return toDelete;
    }

    static void addToStackMap(final String chatId, final Integer msgId) {
        Thread th = new Thread(()->{
            Long chId = null;
            try {
                chId = Long.parseLong(chatId);
            } catch (NumberFormatException nfe) {
                log.error("Chat id " + chatId + " parse error.", nfe);
            }
            if (chId != null) {
                if (stackMap.containsKey(chId)) {
                    Set<Integer> cur = new HashSet<>(stackMap.get(chId));
                    if (!cur.add(msgId)) {
                        log.warn("Stack message duplicate");
                    }
                    stackMap.put(chId, cur);
                } else {
                    stackMap.put(chId, Collections.singleton(msgId));
                }
            }
        });
        th.start();
    }
}
