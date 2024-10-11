package org.example;

import java.util.List;

public class Aggregator {

    // Итерационный цикл
    public static long countMessagesIteratively(List<Chat> chats) {
        long totalMessages = 0;
        for (Chat chat : chats) {
            totalMessages += chat.getMessages().size();
        }
        return totalMessages;
    }

    // Конвейер с Stream API
    public static long countMessagesWithStreams(List<Chat> chats) {
        return chats.stream()
                .flatMap(chat -> chat.getMessages().stream())
                .count();
    }
}
