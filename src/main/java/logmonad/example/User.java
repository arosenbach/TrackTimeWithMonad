package logmonad.example;

import logmonad.util.Random;

public class User {
    private final String id;
    private final int age;

    public User(final String id) {
        this.id = id;
        this.age = Random.getRandomInt(5,37);
    }

    public boolean isAdult(){
        return age >= 21;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", age=" + age +
                '}';
    }
}
