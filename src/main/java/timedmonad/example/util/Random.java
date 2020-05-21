package timedmonad.example.util;

import java.util.Arrays;
import java.util.List;

public class Random {

    private final static List<String> FIRST_NAMES = Arrays.asList("Kellen", "Danna", "Aubree", "Jayvon", "Cecelia", "Piper",
            "Cali", "Cali", "Rodney", "Tyshawn", "Alena", "Brayden", "Juan", "Van", "Yareli", "Raphael", "Timothy",
            "Adonis", "Lilian", "Dakota", "Jakobe", "Reid", "Ross", "Alfredo", "Cora", "Shaun", "Johnny", "Brielle",
            "Raphael", "Eden", "Selina", "Alison", "Nina", "Melissa", "Pierre");
    private final static List<String> LAST_NAMES = Arrays.asList("Stephens", "Zamora", "Wilkerson", "Donovan", "Pruitt", "Hampton",
            "Case", "Bridges", "Riggs", "Bright", "Guzman", "Brown", "Cooley", "Schwartz", "Krause", "Strong", "Gross", "Mccarty",
            "Baker", "Khan", "Lynn", "Burgess", "Nielsen", "Sharp", "Conley", "Rasmussen", "Zimmerman", "Goodman", "Hopkins",
            "Singleton", "Casey", "Everett", "Hanson", "Giles", "Petersen");

    public static int getRandomInt(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static String getRandomName() {
        return FIRST_NAMES.get(getRandomInt(0, FIRST_NAMES.size() - 1)) + " " + LAST_NAMES.get(getRandomInt(0, LAST_NAMES.size() - 1));
    }
}
