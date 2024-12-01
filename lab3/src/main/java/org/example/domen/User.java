package org.example.domen;


import java.time.LocalDateTime;
import java.util.List;

public class User {
    private final String name;
    private final int age;
    private final LocalDateTime registrationDate;
    private final Role role;
    private final ContactInfo contactInfo;
    private final List<User> friends;  // Коллекция друзей

    public User(String name, int age, LocalDateTime registrationDate, Role role, ContactInfo contactInfo, List<User> friends) {
        this.name = name;
        this.age = age;
        this.registrationDate = registrationDate;
        this.role = role;
        this.contactInfo = contactInfo;
        this.friends = friends;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public Role getRole() { return role; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public List<User> getFriends() { return friends; }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", registrationDate=" + registrationDate +
                ", role=" + role +
                ", contactInfo=" + contactInfo +
                ", friends=" + friends +
                '}';
    }
}