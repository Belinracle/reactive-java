package org.example;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Aggregator {

        public static Map<DayOfWeek, Long> countMessagesPerDayIteratively(List<Chat> chats,Long delay) {
        var messageCountPerDay = new HashMap<DayOfWeek, Long>();
        for (DayOfWeek day : DayOfWeek.values()) {
            messageCountPerDay.put(day, 0L);
        }

        for (Chat chat : chats) {
            List<Message> messages;
            if(delay != null) {
                messages = chat.getMessagesWithDelay(delay);
            }else{
                messages = chat.getMessages();
            }
            for (Message message : messages) {
                var messageDayOfWeek = message.getTimestamp().getDayOfWeek();
                messageCountPerDay.put(messageDayOfWeek, messageCountPerDay.get(messageDayOfWeek) + 1L);
            }
        }
        return messageCountPerDay;
    }

    public static Map<DayOfWeek, Long> countMessagesPerDayWithParallelStream(List<Chat> chats, Long delay) {
        return  chats.parallelStream()
                .flatMap(chat -> {
                    List<Message> messages;
                    if(delay != null) {
                        messages = chat.getMessagesWithDelay(delay);
                    }else{
                        messages = chat.getMessages();
                    }
                    return messages.stream();
                })
                .collect(Collectors.groupingByConcurrent(message -> message.getTimestamp().getDayOfWeek(), Collectors.counting()));
    }

    public static Map<DayOfWeek, Long> countMessagesPerDayWithStream(List<Chat> chats, Long delay) {
        return chats.stream()
                .flatMap(chat -> {
                    List<Message> messages;
                    if(delay != null) {
                        messages = chat.getMessagesWithDelay(delay);
                    }else{
                        messages = chat.getMessages();
                    }
                    return messages.stream();
                })
                .collect(Collectors.groupingBy(message -> message.getTimestamp().getDayOfWeek(), Collectors.counting()));
    }
}
