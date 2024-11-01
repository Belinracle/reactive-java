package org.example;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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
}
