package org.example;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Aggregator {

    // Итерационный цикл
    public static long countMessagesIteratively(List<Chat> chats) {
        long totalMessages = 0;

        for (Chat chat : chats) {
            totalMessages += chat.getMessages().size();
        }
        return totalMessages;
    }

    public static Map<DayOfWeek, Long> countMessagesPerDayIteratively(List<Chat> chats) {
        var messageCountPerDay = new HashMap<DayOfWeek, Long>();
        for (DayOfWeek day : DayOfWeek.values()) {
            messageCountPerDay.put(day, 0L);
        }

        for (Chat chat : chats) {
            for (Message message : chat.getMessages()) {
                var messageDayOfWeek = message.getTimestamp().getDayOfWeek();
                messageCountPerDay.put(messageDayOfWeek, messageCountPerDay.get(messageDayOfWeek) + 1L);
            }
        }
        return messageCountPerDay;
    }

    public static Map<DayOfWeek, Long> countMessagesPerDayWithStream(List<Chat> chats) {
        return chats.parallelStream().flatMap(chat -> chat.getMessages().stream())
                .collect(Collectors.groupingByConcurrent(message -> message.getTimestamp().getDayOfWeek(), Collectors.counting()));
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
