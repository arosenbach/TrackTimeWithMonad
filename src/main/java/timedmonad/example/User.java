package timedmonad.example;

import timedmonad.example.util.Random;

public class User {
    private final String id;
    private final int age;
    private final String fullName;

    public User(final String id) {
        this.id = id;
        this.age = Random.getRandomInt(5, 37);
        this.fullName = Random.getRandomName();
    }

    public boolean isAdult() {
        return age >= 21;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", age=" + age +
                '}';
    }
}
