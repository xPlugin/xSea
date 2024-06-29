package pr.lofe.mdr.xsea.util;

import java.util.Random;

public class RandomUtil {

    public static boolean nextBool(double chance) {
        return new Random().nextDouble() < chance / 100;
    }

    public static int nextInt(int max) {
        return new Random().nextInt(max - 1) + 1;
    }
    
}
