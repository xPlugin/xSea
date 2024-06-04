package pr.lofe.mdr.xsea.util;

public class RandomUtil {

    public static boolean nextBool(double chance) {
        return Math.random() < chance / 100;
    }

}
