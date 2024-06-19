package pr.lofe.mdr.xsea.entity;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.entity.level.PlayerLevel;

import java.util.HashMap;

public class DisplayUpdate {

    private static final HashMap<Player, BossBar> showing = new HashMap<>();

    public static void remove() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            BossBar prev = showing.remove(player);
            if(prev != null) {
                player.hideBossBar(prev);
                prev = null;
            }
        }
        System.gc();
    }

    public static void update() {
        remove();
        for(Player player: Bukkit.getOnlinePlayers()) {
            PlayerTask task = TasksRegistry.getCompleting(player);
            int level = PlayerLevel.getLevel(player);

            StringBuilder buffer = new StringBuilder();
            if(task != null) buffer.append("<white><color:#ffd061>Цель</color>: ").append(task.displayName()).append(" | ");
            buffer.append("Уровень: <color:#ffd061>").append(level).append("</color> <color:dark_gray>(");

            if(level == 10) buffer.append("max");
            else {
                buffer.append(PlayerLevel.getPoints(player));
                if(level < 11) buffer.append("/").append(PlayerLevel.getLevelThreshold(level + 1));
            }

            buffer.append(")</color>");

            BossBar bar = BossBar.bossBar(
                    TextWrapper.text(buffer.toString()),
                    0f,
                    BossBar.Color.YELLOW,
                    BossBar.Overlay.PROGRESS
            );

            showing.put(player, bar);

            player.showBossBar(bar);
        }
    }

}
