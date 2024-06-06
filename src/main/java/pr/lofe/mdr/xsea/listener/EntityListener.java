package pr.lofe.mdr.xsea.listener;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.potion.PotionEffectType;

public class EntityListener implements Listener {

    @EventHandler public void tick(ServerTickEndEvent event) {
        for(Player player: Bukkit.getOnlinePlayers()) {
            if(player.isInWater()) {
                if(!player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
                    int air = player.getRemainingAir();
                    if(air > 1 && air < 300 && event.getTickNumber() % 3 == 0) player.setRemainingAir(air + 2);
                }
            }
        }
    }

}
