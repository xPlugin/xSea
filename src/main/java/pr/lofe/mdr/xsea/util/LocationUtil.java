package pr.lofe.mdr.xsea.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class LocationUtil {

    public static double distance(Location pos1, Location pos2) {
        return Math.sqrt(
                Math.pow(pos1.getX() - pos2.getX(), 2) +
                        Math.pow(pos1.getY() - pos2.getY(), 2) +
                        Math.pow(pos1.getZ() - pos2.getZ(), 2)
        );
    }

}
