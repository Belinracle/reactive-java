package org.example;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        var users = Generator.generateUsers(1000);
        List<Chat> chats = Generator.generateChats(1000, users);

        Instant start, end;

//         Итерационный способ
//        start = Instant.now();
//        long totalMessages1 = Aggregator.countMessagesTextIteratively(chats);
//        long totalMessages1 = Aggregator.countMessagesIteratively(chats);
//        end = Instant.now();
//        System.out.println("Итерационно: " + totalMessages1 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        // Stream API
//        start = Instant.now();
//        long totalMessages2 = Aggregator.countMessagesTextWithStreams(chats);
//        long totalMessages2 = Aggregator.countMessagesWithStreams(chats);
//        end = Instant.now();
//        System.out.println("Stream API: " + totalMessages2 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        // Собственный коллектор

        var newChats = List.of(
                new Chat(List.of(
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,14,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,16,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,17,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,18,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,19,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,14,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,14,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,14,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,16,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,17,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,18,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,19,1,1))
                ),List.of()),
                new Chat(List.of(
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,14,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,16,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,17,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,18,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,19,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,20,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,14,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,14,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,14,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,15,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,16,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,17,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,18,1,1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER,19,1,1))
                ),List.of())
        );

        start = Instant.now();
        var totalMessages1 = Aggregator.countMessagesPerDayIteratively(chats);
        end = Instant.now();
        System.out.println("Итеративный коллектор: " + totalMessages1 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        var totalMessages2 = Aggregator.countMessagesPerDayWithParallelStream(chats);
        end = Instant.now();
        System.out.println("Стримовый коллектор: " + totalMessages2 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        Map<DayOfWeek,Long> totalMessages3 = CustomChatCollector.countMessagesWithCustomCollector(chats);
        end = Instant.now();
        System.out.println("Собственный коллектор: " + totalMessages3 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");
    }
}