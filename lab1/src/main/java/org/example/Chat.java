package org.example;

import java.util.List;

public class Chat {
    private final List<Message> messages;
    private final List<User> participants;

    public Chat(List<Message> messages, List<User> participants) {
        this.messages = messages;
        this.participants = participants;
    }

    public List<Message> getMessages() { return messages; }
    public List<User> getParticipants() { return participants; }

    @Override
    public String toString() {
        return "Chat{" +
                "messages=" + messages +
                ", participants=" + participants +
                '}';
    }
}