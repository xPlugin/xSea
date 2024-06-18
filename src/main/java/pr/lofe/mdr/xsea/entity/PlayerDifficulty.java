package pr.lofe.mdr.xsea.entity;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pr.lofe.mdr.xsea.start.StartEngine;
import pr.lofe.mdr.xsea.xSea;

public enum PlayerDifficulty {

    EASY,
    HARD;

    public static PlayerDifficulty getDifficulty(OfflinePlayer player) {
        return PlayerDifficulty.valueOf(xSea.data.getConfig().getString(player.getName() + ".difficulty", "EASY"));
    }

    public static void setDifficulty(Player player, PlayerDifficulty difficulty) {
        xSea.data.getConfig().set(player.getName() + ".difficulty", difficulty.name());
        xSea.data.save();
    }

}
