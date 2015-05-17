package pack;

import java.util.Random;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public class Util {

    // Implementing Fisher–Yates shuffle
    public static void shuffleArray(Object[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Object a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
