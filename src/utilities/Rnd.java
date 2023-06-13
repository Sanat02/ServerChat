package utilities;

import java.util.Random;

public class Rnd {

    private static final Random random = new Random();

    private Rnd() {
    }

    public static int rnd(int bound) {
        return random.nextInt(bound);
    }
}
