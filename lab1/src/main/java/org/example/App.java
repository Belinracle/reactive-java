package org.example;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var users = Generator.generateUsers(500);
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
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 14, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 16, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 17, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 18, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 19, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 14, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 14, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 14, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 16, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 17, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 18, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 19, 1, 1))
                ), List.of()),
                new Chat(List.of(
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 14, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 16, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 17, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 18, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 19, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 20, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 14, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 14, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 14, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 15, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 16, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 17, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 18, 1, 1)),
                        new Message("1", users.get(0), LocalDateTime.of(2024, Month.OCTOBER, 19, 1, 1))
                ), List.of())
        );

        start = Instant.now();
        var totalMessages1 = Aggregator.countMessagesPerDayIteratively(chats, 0L);
        end = Instant.now();
        System.out.println("Итеративный коллектор: " + totalMessages1 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        var totalMessages2 = Aggregator.countMessagesPerDayWithParallelStream(chats, 0L);
        end = Instant.now();
        System.out.println("Стримовый коллектор: " + totalMessages2 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        Map<DayOfWeek, Long> totalMessages3 = CustomChatCollector.countMessagesWithCustomCollector(chats, 0L);
        end = Instant.now();
        System.out.println("Собственный коллектор: " + totalMessages3 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        System.out.println("Подсчёт времени с задержкой:");

        start = Instant.now();
        var totalMessages11 = Aggregator.countMessagesPerDayIteratively(chats, 100L);
        end = Instant.now();
        System.out.println("Итеративный коллектор: " + totalMessages11 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        var totalMessages21 = Aggregator.countMessagesPerDayWithParallelStream(chats, 100L);
        end = Instant.now();
        System.out.println("Стримовый коллектор: " + totalMessages21 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        Map<DayOfWeek, Long> totalMessages31 = CustomChatCollector.countMessagesWithCustomCollector(chats, 100L);
        end = Instant.now();
        System.out.println("Собственный коллектор: " + totalMessages31 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        Map<DayOfWeek, Long> totalMessages32 =  CustomChatCollector.countMessagesWithCustomCollectorOnCustomThreadPool2(chats,100L);
        end = Instant.now();
        System.out.println("Собственный коллектор на кастомном тред пуле 2: " + totalMessages32 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        Map<DayOfWeek, Long> totalMessages33 =  CustomChatCollector.countMessagesWithCustomCollectorOnCustomThreadPool4(chats,100L);
        end = Instant.now();
        System.out.println("Собственный коллектор на кастомном тред пуле 4: " + totalMessages33 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        Map<DayOfWeek, Long> totalMessages34 =  CustomChatCollector.countMessagesWithCustomCollectorOnCustomThreadPool10(chats,100L);
        end = Instant.now();
        System.out.println("Собственный коллектор на кастомном тред пуле 10: " + totalMessages34 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        Map<DayOfWeek, Long> totalMessages35 =  CustomChatCollector.countMessagesWithCustomCollectorOnCustomThreadPool10AndCustomSpliterator(chats,100L);
        end = Instant.now();
        System.out.println("Собственный коллектор на кастомном тред пуле 10 кастомным сплитератором: " + totalMessages35 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        start = Instant.now();
        Map<DayOfWeek, Long> totalMessages36 =  CustomChatCollector.countMessagesWithCustomCollectorOnCustomThreadPool10AndCustomSpliteratorAnotherWay(chats,100L);
        end = Instant.now();
        System.out.println("Собственный коллектор на кастомном тред пуле 10 кастомным сплитератором немного по другому: " + totalMessages36 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

    }
}