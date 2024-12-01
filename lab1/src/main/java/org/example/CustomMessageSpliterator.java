package org.example;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class CustomMessageSpliterator implements Spliterator<Message> {
    private final List<Message> elements;
    private int currentIndex;

    public CustomMessageSpliterator(List<Message> elements) {
        this.elements = elements;
        this.currentIndex = 0;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Message> action) {
        if (currentIndex < elements.size()) {
            action.accept(elements.get(currentIndex));
            currentIndex++;
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<Message> trySplit() {
        int currentSize = elements.size() - currentIndex;
        if (currentSize < 1000) {
            return null;
        }

        int splitIndex = currentIndex + currentSize / 2;
        CustomMessageSpliterator newSpliterator = new CustomMessageSpliterator(elements.subList(currentIndex, splitIndex));
        currentIndex = splitIndex;
        return newSpliterator;
    }

    @Override
    public long estimateSize() {
        return elements.size() - currentIndex;
    }

    @Override
    public int characteristics() {
        return SIZED | SUBSIZED | NONNULL;
    }
}