package pr.lofe.mdr.xsea.util;

import net.minecraft.util.RandomSource;

public class RandomUtil {

    private final static RandomSource random = RandomSource.create(624549675007745075L);

    public static boolean nextBool(double chance) {
        return random.nextDouble() < chance / 100;
    }

}
