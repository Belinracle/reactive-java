package org.example.statistics;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.example.domen.Chat;
import org.example.domen.Message;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Calculator {
    public static Map<DayOfWeek, Long> calculateStatisticsParStreams(List<Chat> chats, Long delay) {
        return chats.parallelStream()
                .flatMap(chat -> {
                    List<Message> messages;
                    if (delay != null) {
                        messages = chat.getMessagesWithDelay(delay);
                    } else {
                        messages = chat.getMessages();
                    }
                    return messages.stream();
                })
                .collect(Collectors.groupingByConcurrent(message -> message.getTimestamp().getDayOfWeek(), Collectors.counting()));
    }

    public static Map<DayOfWeek, Long> calculateStatisticsByObservable(List<Chat> chats, Long delay) {
        return Observable.fromIterable(chats)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(chat -> Observable.fromIterable(chat.getMessagesWithDelay(delay)))
                .observeOn(Schedulers.computation())
                .collect(Collectors.groupingByConcurrent(message -> message.getTimestamp().getDayOfWeek(), Collectors.counting()))
                .blockingGet();
    }

    public static Map<DayOfWeek, Long> calculateStatisticsByFlowableWithBackpressure(List<Chat> chats, Long delay) {
        return Flowable.fromIterable(chats)
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .flatMap(chat -> Flowable.fromIterable(chat.getMessagesWithDelay(1L)))
                .observeOn(Schedulers.computation())
                .to(new MessageFlowableConverter(1000L))
                .blockingGet();
    }
}
