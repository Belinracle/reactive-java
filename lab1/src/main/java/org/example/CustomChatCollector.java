package org.example;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.Characteristics.UNORDERED;

public class CustomChatCollector implements Collector<Chat, Map<DayOfWeek, Long>, Map<DayOfWeek, Long>> {

    @Override
    public Supplier<Map<DayOfWeek, Long>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<DayOfWeek, Long>, Chat> accumulator() {
        return (accumulator, chat) -> {
            var messagesMap = chat.getMessages().stream().collect(new CustomMessageCollector());
            for (DayOfWeek day : DayOfWeek.values()) {
                var messagesMapValue = messagesMap.get(day) == null ? 0 : messagesMap.get(day);
                accumulator.compute(day, (k, v) -> (v == null) ? messagesMapValue : v + messagesMapValue);
            }
        };
    }

    @Override
    public BinaryOperator<Map<DayOfWeek, Long>> combiner() {
        return (map1, map2) -> Stream.concat(map1.entrySet().stream(), map2.entrySet().stream()).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
    }

    @Override
    public Function<Map<DayOfWeek, Long>, Map<DayOfWeek, Long>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(UNORDERED, IDENTITY_FINISH);
    }

    public static Map<DayOfWeek, Long> countMessagesWithCustomCollector(List<Chat> chats) {
        return chats.stream().collect(new CustomChatCollector());
    }
}