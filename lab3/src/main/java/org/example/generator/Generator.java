package org.example.generator;

import org.example.domen.*;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.instancio.Select.field;

public class Generator {
    public static List<User> generateUsers(int count) {
        return Instancio.ofList(User.class).size(count).create();
    }

    public static List<Message> generateMessages(int count, List<User> users) {
        return Instancio.ofList(Message.class)
                .size(count)
                .generate(field(Message::getAuthor), gen -> gen.oneOf(users))
                .generate(field(Message::getTimestamp), gen -> gen.temporal().localDateTime().past())
                .create();
    }

    public static List<Chat> generateChats(int count, List<User> users) {
        Model<Chat> model = Instancio.of(Chat.class)
                .supply(field(Chat::getParticipants), () -> generateParticipants(users))
                .supply(field(Chat::getMessages), () -> generateMessages(count, users))
                .toModel();

        return Instancio.ofList(model).size(count).create();
    }

    // Генератор случайного списка участников
    private static List<User> generateParticipants(List<User> users) {
        // Генерация случайного количества участников (от 3 до 10)
        int participantsCount = 3 + (int) (Math.random() * (10 - 3 + 1));
        return IntStream.range(0, participantsCount)
                .mapToObj(i -> users.get((int) (Math.random() * users.size())))
                .collect(Collectors.toList());
    }
}