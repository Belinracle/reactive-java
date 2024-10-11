package org.example;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CustomCollector implements Collector<Chat, AtomicLong, Long> {

    @Override
    public Supplier<AtomicLong> supplier() {
        return AtomicLong::new;
    }

    @Override
    public BiConsumer<AtomicLong, Chat> accumulator() {
        return (total, chat) -> total.addAndGet(chat.getMessages().size());
    }

    @Override
    public BinaryOperator<AtomicLong> combiner() {
        return (left, right) -> {
            left.addAndGet(right.get());
            return left;
        };
    }

    @Override
    public Function<AtomicLong, Long> finisher() {
        return AtomicLong::get;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }

    public static long countMessagesWithCustomCollector(List<Chat> chats) {
        return chats.stream().collect(new CustomCollector());
    }
}
