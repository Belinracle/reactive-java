package org.example.statistics;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.subjects.SingleSubject;
import org.example.domen.Message;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class MessageFlowableConverter implements FlowableConverter<Message, Single<Map<DayOfWeek, Long>>>, Subscriber<Message> {
    private Map<DayOfWeek, Long> map = new HashMap<>();
    private SingleSubject<Map<DayOfWeek, Long>> single = SingleSubject.create();
    private Subscription subscription;
    private Long batchSize;
    private Long processedElementsCounter = 0L;

    public MessageFlowableConverter(Long batchSize) {
        this.batchSize = batchSize;
        for (var day : DayOfWeek.values()) {
            map.put(day, 0L);
        }
    }

    @Override
    public void onSubscribe(@NonNull Subscription s) {
        this.subscription = s;
        subscription.request(batchSize);
    }

    @Override
    public void onNext(Message message) {
        map.put(message.getTimestamp().getDayOfWeek(), map.get(message.getTimestamp().getDayOfWeek()) + 1L);
        processedElementsCounter++;
        if (processedElementsCounter % batchSize == 0) {
            subscription.request(batchSize);
        }
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {
        single.onSuccess(map);
    }

    @Override
    public Single<Map<DayOfWeek, Long>> apply(@NonNull Flowable<Message> upstream) {
        upstream.subscribe(this);
        return single;
    }
}
