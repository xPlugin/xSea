package pr.lofe.mdr.xsea.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    public static boolean nextBool(double chance) {
        return Math.random() < chance / 100;
    }

    public static int nextInt(int max) {
        return new Random().nextInt(max - 1) + 1;
    }

}
