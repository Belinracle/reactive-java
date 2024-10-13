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

    public static long countMessagesTextIteratively(List<Chat> chats) {
        long totalMessages = 0;
        for (Chat chat : chats) {
            List<Message> messages = chat.getMessages();
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getText().length() > 5) {
                    totalMessages++;
                }
            }
        }
        return totalMessages;
    }

    // Конвейер с Stream API
    public static long countMessagesWithStreams(List<Chat> chats) {
        return chats.stream()
                .flatMap(chat -> chat.getMessages().stream())
                .count();
    }

    public static long countMessagesTextWithStreams(List<Chat> chats) {
        return chats.stream()
                .flatMap(chat -> chat.getMessages().stream())
                .filter(message -> message.getText().length() > 5)
                .count();
    }
}
