package org.example;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.*;

//https://www.amitph.com/java-streams-custom-collector/
public class CustomMessageCollector implements Collector<Message, Map<DayOfWeek, Long>, Map<DayOfWeek, Long>> {

    @Override
    public Supplier<Map<DayOfWeek, Long>> supplier() {
        var predefinedMap = new ConcurrentHashMap<DayOfWeek, Long>();
        for (var day : DayOfWeek.values()) {
            predefinedMap.put(day, 0L);
        }
        return () -> predefinedMap;
    }

    @Override
    public BiConsumer<Map<DayOfWeek, Long>, Message> accumulator() {
        return (accumulator, message) -> {
            accumulator.computeIfPresent(message.getTimestamp().getDayOfWeek(), (key, oldValue) -> oldValue + 1);
        };
    }

    // можно попробовать разные варианты
    // https://www.baeldung.com/java-merge-maps
    @Override
    public BinaryOperator<Map<DayOfWeek, Long>> combiner() {
        return (map1, map2) -> {
            map2.forEach(
                    (map2Key, map2Value) -> {
                        map1.compute(map2Key, (k, v) -> v + map2Value);
                    });
            return map1;
        };
    }

    @Override
    public Function<Map<DayOfWeek, Long>, Map<DayOfWeek, Long>> finisher() {
        return null;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(UNORDERED, CONCURRENT, IDENTITY_FINISH);
    }

}
