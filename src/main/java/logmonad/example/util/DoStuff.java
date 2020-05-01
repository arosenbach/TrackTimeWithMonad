package logmonad.example.util;

public class DoStuff {

    public static void sleep() {
        sleep(Random.getRandomInt(10,500));
    }

    public static void sleep(final int randomInt) {
        try {
            Thread.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
