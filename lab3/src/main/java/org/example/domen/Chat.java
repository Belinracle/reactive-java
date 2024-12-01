package org.example.domen;

import java.util.List;

public class Chat {
    private final List<Message> messages;
    private final List<User> participants;

    public Chat(List<Message> messages, List<User> participants) {
        this.messages = messages;
        this.participants = participants;
    }

    public List<User> getParticipants() { return participants; }

    public List<Message> getMessages() { return messages; }

    public List<Message> getMessagesWithDelay(long delay) {
        try {
            // Задержка для имитации ожидания результата
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Сохраняем статус прерывания потока
            throw new RuntimeException("Interrupted while waiting", e);
        }
        return messages;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "messages=" + messages +
                ", participants=" + participants +
                '}';
    }
}