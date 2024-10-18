package org.example;

import java.time.DayOfWeek;
import java.util.HashMap;
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

//https://www.amitph.com/java-streams-custom-collector/
public class CustomMessageCollector implements Collector<Message, Map<DayOfWeek, Long>, Map<DayOfWeek, Long>> {

    @Override
    public Supplier<Map<DayOfWeek, Long>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<DayOfWeek, Long>, Message> accumulator() {
        return (accumulator, message) -> accumulator.compute(message.getTimestamp().getDayOfWeek(), (key, val)
                -> (val == null)
                ? 1
                : val + 1);
    }

    // можно попробовать разные варианты
    // https://www.baeldung.com/java-merge-maps
    @Override
    public BinaryOperator<Map<DayOfWeek, Long>> combiner() {
        return (map1, map2) -> Stream.concat(map1.entrySet().stream(), map2.entrySet().stream()).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
    }

    @Override
    public Function<Map<DayOfWeek, Long>, Map<DayOfWeek, Long>> finisher() {
        return null;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(UNORDERED,IDENTITY_FINISH);
    }

}
