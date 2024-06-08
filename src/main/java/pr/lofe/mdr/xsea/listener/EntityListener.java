package pr.lofe.mdr.xsea.listener;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pr.lofe.mdr.xsea.xSea;

public class EntityListener implements Listener {

    @EventHandler public void tick(ServerTickEndEvent event) {
        for(Player player: Bukkit.getOnlinePlayers()) {
            if(player.isInWater()) {
                ItemStack item = player.getInventory().getBoots(); // getBoobs
                if(xSea.getItems().getKey(item).equals("flippers")) {
                    PotionEffect effect = player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE);
                    if(effect == null) effect = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 2, 0, true, false, false);
                    else if (!effect.hasIcon()) effect = effect.withDuration(2);

                    player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
                    player.addPotionEffect(effect);
                }
            }
        }
    }

}
