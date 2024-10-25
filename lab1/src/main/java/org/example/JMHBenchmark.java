package org.example;

import org.openjdk.jmh.annotations.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//https://habr.com/ru/companies/sberbank/articles/814299/
@State(Scope.Thread)
public class JMHBenchmark {
    private List<User> users;
    private List<Chat> chats;

    @Setup
    public void setup() {
        users = Generator.generateUsers(1000);
        chats = Generator.generateChats(1000, users);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
//    @Threads(4)
    public Map<DayOfWeek, Long> iterative() {
        return Aggregator.countMessagesPerDayIteratively(chats);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
//    @Threads(4)
    public Map<DayOfWeek, Long> stream() {
        return Aggregator.countMessagesPerDayWithStream(chats);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
    @Threads(4)
    public Map<DayOfWeek, Long> streamParallel4Threads() {
        return Aggregator.countMessagesPerDayWithParallelStream(chats);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
    @Threads(1)
    public Map<DayOfWeek, Long> streamParallel1Threads() {
        return Aggregator.countMessagesPerDayWithParallelStream(chats);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
    @Threads(4)
    public Map<DayOfWeek, Long> streamCustomCollector4Threads() {
        return CustomChatCollector.countMessagesWithCustomCollector(chats);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
    @Threads(1)
    public Map<DayOfWeek, Long> streamCustomCollector1Threads() {
        return CustomChatCollector.countMessagesWithCustomCollector(chats);
    }

}
