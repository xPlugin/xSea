package pr.lofe.mdr.xsea.entity;

import org.bukkit.entity.Player;
import pr.lofe.mdr.xsea.config.Config;

public enum PlayerDifficulty {

    EASY,
    HARD;

    private final static Config data = new Config("data", false, false);
    public static PlayerDifficulty getDifficulty(Player player) {
        return PlayerDifficulty.valueOf(data.getConfig().getString(player.getName() + ".difficulty", "EASY"));
    }

    public static void setDifficulty(Player player, PlayerDifficulty difficulty) {
        data.getConfig().set(player.getName() + ".difficulty", difficulty);
    }

}
