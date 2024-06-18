package pr.lofe.mdr.xsea.entity;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pr.lofe.lib.xbase.text.TextWrapper;

import java.util.HashMap;

public class DisplayUpdate {

    private static final HashMap<Player, BossBar> showing = new HashMap<>();

    public static void update() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            BossBar prev = showing.remove(player);
            if(prev != null) {
                player.hideBossBar(prev);
                prev = null;
            }

            PlayerTask task = TasksRegistry.getCompleting(player);
            int level = PlayerLevel.getLevel(player);

            StringBuilder buffer = new StringBuilder();
            if(task != null) buffer.append("<white><color:#ffd061>Цель</color>: ").append(task.displayName()).append(" | ");
            buffer.append("Уровень: <color:#ffd061>").append(level).append("</color>");
            BossBar bar = BossBar.bossBar(
                    TextWrapper.text(buffer.toString()),
                    0f,
                    BossBar.Color.YELLOW,
                    BossBar.Overlay.PROGRESS
            );

            showing.put(player, bar);

            player.showBossBar(bar);
        }
        System.gc();
    }

}
