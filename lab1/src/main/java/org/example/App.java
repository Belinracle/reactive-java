package org.example;

import org.instancio.Instancio;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        List<User> persons = Instancio.ofList(User.class).size(10).create();
        List<User> message = Instancio.of(User.class).size(10).create();
        System.out.println(persons);
    }
}

enum Town{
    MOSCOW,
    SAINT_P,
    KAZAN
}

record User(String name, int age, Town registered, long birthDate){}

record Message(long sendTime, String content, User author){ }