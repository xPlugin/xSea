package pr.lofe.mdr.xsea.debug;

import org.bukkit.entity.Player;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.xSea;

public class DebugMode {

    public static void setEnabled(Player player, boolean state) {
        Config data = xSea.data;
        data.getConfig().set(player.getName() + ".debug", state);
        data.save();
    }

    public static boolean isEnabled(Player player) {
        return xSea.data.getConfig().getBoolean(player.getName() + ".debug", false);
    }

}
