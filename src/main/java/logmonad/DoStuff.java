package logmonad;

public class DoStuff {

    public static void run() {
        run(getRandomInt(10,500));
    }

    public static void run(final int randomInt) {
        try {
            Thread.sleep(22);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getRandomInt(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
}
