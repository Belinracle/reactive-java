package org.example;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.HashMap;

public class CustomCollector implements Collector<String, Map<String, Long>, Map<String, Long>> {

    @Override
    public Supplier<Map<String, Long>> supplier() {
        return HashMap::new; // Используем HashMap для накопления результатов
    }

    @Override
    public BiConsumer<Map<String, Long>, String> accumulator() {
        return (map, key) -> {
            if (key.length() > 5) { // Проверяем длину текста
                map.merge(key, 1L, Long::sum); // Увеличиваем счетчик для данного текста
            }
        };
    }

    @Override
    public BinaryOperator<Map<String, Long>> combiner() {
        return (map1, map2) -> {
            map2.forEach((key, value) -> map1.merge(key, value, Long::sum));
            return map1;
        };
    }

    @Override
    public Function<Map<String, Long>, Map<String, Long>> finisher() {
        return Function.identity(); // Не нужно дополнительное преобразование
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(); // Характеристики коллектора не заданы
    }

    public static Map<String, Long> countMessagesWithCustomCollector(List<Chat> chats) {
        // Собираем сообщения из всех чатов, фильтруем по длине текста > 5 и считаем их
        return chats.stream()
                .flatMap(chat -> chat.getMessages().stream().map(Message::getText)) // Получаем все сообщения
                .collect(new CustomCollector()); // Используем кастомный коллектор для подсчета
    }
}
