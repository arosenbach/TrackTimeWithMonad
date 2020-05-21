package timedmonad.example.util;

public class DoStuff {

    public static void takeSomeTime() {
        takeSomeTime(Random.getRandomInt(10,500));
    }

    public static void takeSomeTime(final int randomInt) {
        try {
            Thread.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
