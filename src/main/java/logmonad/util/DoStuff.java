package logmonad.util;

public class DoStuff {

    public static void run() {
        run(Random.getRandomInt(10,500));
    }

    public static void run(final int randomInt) {
        try {
            Thread.sleep(22);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
