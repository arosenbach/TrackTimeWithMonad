package logmonad;

public class DoStuff {

    public static void run() {
        final int randomInt = getRandomInt(10,500);
        try {
            Thread.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int getRandomInt(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
}
