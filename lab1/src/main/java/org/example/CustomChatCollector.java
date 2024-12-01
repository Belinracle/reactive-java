package org.example;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collector.Characteristics.*;

public class CustomChatCollector implements Collector<Chat, ConcurrentMap<DayOfWeek, Long>, ConcurrentMap<DayOfWeek, Long>> {

    private final long delay;

    // Конструктор с параметром задержки
    public CustomChatCollector(long delay) {
        this.delay = delay;
    }

    @Override
    public Supplier<ConcurrentMap<DayOfWeek, Long>> supplier() {
        var predefinedMap = new ConcurrentHashMap<DayOfWeek, Long>();
        for (var day : DayOfWeek.values()) {
            predefinedMap.put(day, 0L);
        }
        return () -> predefinedMap;
    }

    @Override
    public BiConsumer<ConcurrentMap<DayOfWeek, Long>, Chat> accumulator() {
        return (accumulator, chat) -> {
            // Используем задержку для вызова правильного метода
            var messages = (delay > 0) ? chat.getMessagesWithDelay(delay) : chat.getMessages();

            // Считаем сообщения по дням
            var messageCountPerDay = messages.stream().parallel().collect(new CustomMessageCollector());

            // Добавляем результат в accumulator
            messageCountPerDay.forEach((day, count) ->
                    accumulator.merge(day, count, Long::sum)
            );
        };
    }

    @Override
    public BinaryOperator<ConcurrentMap<DayOfWeek, Long>> combiner() {
        return (map1, map2) -> {
            map2.forEach((map2Key, map2Value) ->
                    map1.merge(map2Key, map2Value, Long::sum));
            return map1;
        };
    }

    @Override
    public Function<ConcurrentMap<DayOfWeek, Long>, ConcurrentMap<DayOfWeek, Long>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(UNORDERED, CONCURRENT, IDENTITY_FINISH);
    }

    public static ConcurrentMap<DayOfWeek, Long> countMessagesWithCustomCollector(List<Chat> chats, long delay) {
        return chats.stream()
                .parallel()
                .collect(new CustomChatCollector(delay));
    }

    public static ConcurrentMap<DayOfWeek, Long> countMessagesWithCustomCollectorOnCustomThreadPool2(List<Chat> chats, long delay) throws ExecutionException, InterruptedException {
        ForkJoinPool custom = new ForkJoinPool(2);
        return custom.submit(() -> chats.stream()
                .parallel()
                .collect(new CustomChatCollector(delay))).get();
    }

    public static ConcurrentMap<DayOfWeek, Long> countMessagesWithCustomCollectorOnCustomThreadPool4(List<Chat> chats, long delay) throws ExecutionException, InterruptedException {
        ForkJoinPool custom = new ForkJoinPool(4);
        return custom.submit(() -> chats.stream()
                .parallel()
                .collect(new CustomChatCollector(delay))).get();
    }

    public static ConcurrentMap<DayOfWeek, Long> countMessagesWithCustomCollectorOnCustomThreadPool10(List<Chat> chats, long delay) throws ExecutionException, InterruptedException {
        ForkJoinPool custom = new ForkJoinPool(8);
        return custom.submit(() -> chats.stream()
                .parallel()
                .collect(new CustomChatCollector(delay))).get();
    }

    public static ConcurrentMap<DayOfWeek, Long> countMessagesWithCustomCollectorOnCustomThreadPool10AndCustomSpliterator(List<Chat> chats, long delay) throws ExecutionException, InterruptedException {
        var spliter = new CustomMessageSpliterator(chats.parallelStream().flatMap(chat -> chat.getMessagesWithDelay(delay).stream()).collect(Collectors.toList()));
        ForkJoinPool custom = new ForkJoinPool(10);
        var predefinedMap = new ConcurrentHashMap<DayOfWeek, Long>();
        for (var day : DayOfWeek.values()) {
            predefinedMap.put(day, 0L);
        }
        custom.submit(() -> spliter.forEachRemaining(message -> {
            var dayOfWeek = message.getTimestamp().getDayOfWeek();
            predefinedMap.put(dayOfWeek, 1 + predefinedMap.get(dayOfWeek));
        })).join();
        return predefinedMap;
    }

    public static ConcurrentMap<DayOfWeek, Long> countMessagesWithCustomCollectorOnCustomThreadPool10AndCustomSpliteratorAnotherWay(List<Chat> chats, long delay) throws ExecutionException, InterruptedException {
        var spliter = new CustomChatSpliterator(chats);
        ForkJoinPool custom = new ForkJoinPool(10);
        ForkJoinPool custom2 = new ForkJoinPool(10);
        var predefinedMap = new ConcurrentHashMap<DayOfWeek, Long>();
        for (var day : DayOfWeek.values()) {
            predefinedMap.put(day, 0L);
        }
        custom.submit(() -> spliter.forEachRemaining(chat -> {
            custom2.submit(() -> new CustomMessageSpliterator(chat.getMessagesWithDelay(delay)).forEachRemaining(message -> {
                var dayOfWeek = message.getTimestamp().getDayOfWeek();
                predefinedMap.put(dayOfWeek, 1 + predefinedMap.get(dayOfWeek));
            })).join();
        })).join();
        return predefinedMap;
    }
}
