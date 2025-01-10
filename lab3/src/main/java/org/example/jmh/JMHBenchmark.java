package org.example.jmh;

import org.example.domen.Chat;
import org.example.domen.User;
import org.example.generator.Generator;
import org.example.statistics.Calculator;
import org.openjdk.jmh.annotations.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//https://habr.com/ru/companies/sberbank/articles/814299/
@Fork(1)
@Threads(8)
@State(Scope.Benchmark)
@Warmup(iterations = 0)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMHBenchmark {
    private List<User> users;
    private List<Chat> chats;
    private static long delay = 1L;
    @Param({"500", "2000", "10000"})
    private int chatCount;

    @Setup
    public void setup() {
        System.out.println("start generating");
        users = Generator.generateUsers(100);
        chats = Generator.generateChats(chatCount, users);
        System.out.println("Generated " + chatCount + " chats");
    }

//    @Benchmark
//    public Map<DayOfWeek, Long> benchStream() {
//        return Calculator.calculateStatisticsParStreams(chats, delay);
//    }

    @Benchmark
    public Map<DayOfWeek, Long> benchObservable() {
        return Calculator.calculateStatisticsByObservable(chats, delay);
    }

//    @Benchmark
//    public Map<DayOfWeek, Long> benchFlowable() {
//        return Calculator.calculateStatisticsByFlowableWithBackpressure(chats, delay);
//    }
//
    @Benchmark
    public Map<DayOfWeek, Long> benchVirtualThread(){
        return Calculator.calculateStatisticsByObservableOnVirtualThreads(chats, delay);
    }
}
