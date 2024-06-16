package pr.lofe.mdr.xsea.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import pr.lofe.mdr.xsea.registry.ItemRegistry;
import pr.lofe.mdr.xsea.xSea;

public class ItemListener implements Listener {

    @EventHandler public void onFurnaceSmelt(FurnaceBurnEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        String id = xSea.getItems().getKey(furnace.getInventory().getSmelting());
        if(furnace.getInventory().getSmelting().getType() == Material.COOKED_PORKCHOP && (id == null || !id.equals("raw_titanium"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        String id = xSea.getItems().getKey(event.getSource());
        if(event.getSource().getType() == Material.COOKED_PORKCHOP && (id == null || !id.equals("raw_titanium"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();
        String id = xSea.getItems().getKey(stack);
        if(id != null && stack.getItemMeta().getPersistentDataContainer().has(ItemRegistry.pluginID)) {
            item.setItemStack(stack);
        }
    }

    public void itemStep() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            for(ItemStack item: player.getInventory().getContents()) {
                String id = xSea.getItems().getKey(item);
                if(!"NULL".equals(id)) {
                    NamespacedKey key = NamespacedKey.fromString("modoru:" + id);
                    if(key == null) return;

                    Advancement tmp = Bukkit.getAdvancement(key);
                    if(tmp != null) {
                        AdvancementProgress adv = player.getAdvancementProgress(tmp);
                        for(String str: tmp.getCriteria()) adv.awardCriteria(str);
                    }
                }
            }
        }
    }

}
