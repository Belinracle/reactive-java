package org.example;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            var messageCountPerDay = chat.getMessages().stream().parallel().collect(new CustomMessageCollector());
            messageCountPerDay.forEach(
                    (k, v) ->
                            accumulator.computeIfPresent(k,(key,oldValue)->oldValue+v)
            );
        };
    }

    @Override
    public BinaryOperator<ConcurrentMap<DayOfWeek, Long>> combiner() {
        return (map1, map2) -> {
            map2.forEach(
                    (map2Key, map2Value) -> {
                        map1.compute(map2Key, (k, v) -> v + map2Value);
                    });
            return map1;
        };
    }

    @Override
    public Function<ConcurrentMap<DayOfWeek, Long>, ConcurrentMap<DayOfWeek, Long>> finisher() {
        return null;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(UNORDERED, CONCURRENT, IDENTITY_FINISH);
    }


    //https://habr.com/ru/companies/otus/articles/338770/ как юзать кастомный FJPool
    public static Map<DayOfWeek, Long> countMessagesWithCustomCollector(List<Chat> chats) {
        return chats.stream()
                .parallel()
                .collect(new CustomChatCollector());
    }
}