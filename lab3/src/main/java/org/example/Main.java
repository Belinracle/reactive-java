package org.example;

import org.example.generator.Generator;
import org.example.jmh.JMHBenchmark;
import org.example.statistics.Calculator;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


public class Main {
    public static void main(String[] args) throws RunnerException {

        var chats = Generator.generateChats(1000,Generator.generateUsers(10));
        var resultStreams = Calculator.calculateStatisticsParStreams(chats,1L);
        var resultObservable = Calculator.calculateStatisticsByObservable(chats,1L);
        var resultFlowable = Calculator.calculateStatisticsByFlowableWithBackpressure(chats,1L);
        System.out.println(resultStreams);
        System.out.println(resultObservable);
        System.out.println("flowable result " + resultFlowable);
//        Options opt = new OptionsBuilder()
//                .include(JMHBenchmark.class.getSimpleName())
//                .build();
//        new Runner(opt).run();
    }
}