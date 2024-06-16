package pr.lofe.mdr.xsea.entity;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.start.StartEngine;

public enum PlayerDifficulty {

    EASY,
    HARD;

    public static PlayerDifficulty getDifficulty(OfflinePlayer player) {
        return PlayerDifficulty.valueOf(StartEngine.data.getConfig().getString(player.getName() + ".difficulty", "EASY"));
    }

    public static void setDifficulty(Player player, PlayerDifficulty difficulty) {
        StartEngine.data.getConfig().set(player.getName() + ".difficulty", difficulty.name());
        StartEngine.data.save();
    }

}
