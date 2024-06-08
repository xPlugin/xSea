package pr.lofe.mdr.xsea.listener;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pr.lofe.mdr.xsea.item.ItemRegistry;
import pr.lofe.mdr.xsea.xSea;

public class ItemListener implements Listener {

    @EventHandler public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();
        ItemMeta meta = stack.getItemMeta();
        if(meta != null && meta.hasCustomModelData()) {
            if(meta.getPersistentDataContainer().has(ItemRegistry.pluginID)) {
                ItemStack newItem = xSea.getItems().getItemByCMD(meta.getCustomModelData());
                if(newItem != null) item.setItemStack(newItem);
            }
        }
    }

    public void itemStep() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            for(ItemStack item: player.getInventory().getContents()) {
                String id = xSea.getItems().getKey(item);
                if(!"NULL".equals(id)) {
                    Advancement tmp = Bukkit.getAdvancement(NamespacedKey.fromString("modoru:" + id));
                    if(tmp != null) {
                        AdvancementProgress adv = player.getAdvancementProgress(tmp);
                        for(String str: tmp.getCriteria()) adv.awardCriteria(str);
                    }
                }
            }
        }
    }

}
