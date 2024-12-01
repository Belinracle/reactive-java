package org.example.domen;

import java.time.LocalDateTime;

public class Message {
    private final String text;
    private final User author;
    private final LocalDateTime timestamp;

    public Message(String text, User author, LocalDateTime timestamp) {
        this.text = text;
        this.author = author;
        this.timestamp = timestamp;
    }

    public String getText() { return text; }
    public User getAuthor() { return author; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }
}