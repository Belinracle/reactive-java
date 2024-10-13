package org.example;

import java.time.Instant;
import java.util.List;

public class App {
    public static void main(String[] args) {
        List<Chat> chats = Generator.generateChats(50000, Generator.generateUsers(1000));

        Instant start, end;

        // Итерационный способ
        start = Instant.now();
        long totalMessages1 = Aggregator.countMessagesTextIteratively(chats);
//        long totalMessages1 = Aggregator.countMessagesIteratively(chats);
        end = Instant.now();
        System.out.println("Итерационно: " + totalMessages1 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        // Stream API
        start = Instant.now();
        long totalMessages2 = Aggregator.countMessagesTextWithStreams(chats);
//        long totalMessages2 = Aggregator.countMessagesWithStreams(chats);
        end = Instant.now();
        System.out.println("Stream API: " + totalMessages2 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");

        // Собственный коллектор
        start = Instant.now();
        long totalMessages3 = CustomCollector.countMessagesWithCustomCollector(chats).size();
        end = Instant.now();
        System.out.println("Собственный коллектор: " + totalMessages3 + " сообщений. Время: " + (end.toEpochMilli() - start.toEpochMilli()) + " мс");
    }
}