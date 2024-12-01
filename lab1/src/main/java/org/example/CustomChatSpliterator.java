package org.example;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class CustomChatSpliterator implements Spliterator<Chat> {
    private final List<Chat> elements;
    private int currentIndex;

    public CustomChatSpliterator(List<Chat> elements) {
        this.elements = elements;
        this.currentIndex = 0;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Chat> action) {
        if (currentIndex < elements.size()) {
            action.accept(elements.get(currentIndex));
            currentIndex++;
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<Chat> trySplit() {
        int currentSize = elements.size() - currentIndex;
        if (currentSize < 100) {
            return null;
        }

        int splitIndex = currentIndex + currentSize / 2;
        CustomChatSpliterator newSpliterator = new CustomChatSpliterator(elements.subList(currentIndex, splitIndex));
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