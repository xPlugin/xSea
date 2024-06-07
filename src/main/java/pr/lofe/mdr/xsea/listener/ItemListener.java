package pr.lofe.mdr.xsea.listener;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pr.lofe.mdr.xsea.xSea;

public class ItemListener implements Listener {

    @EventHandler public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();
        ItemMeta meta = stack.getItemMeta();
        if(meta != null && meta.hasCustomModelData()) {
            ItemStack newItem = xSea.getItems().getItemByCMD(meta.getCustomModelData());
            if(newItem != null) item.setItemStack(newItem);
        }
    }

    public void itemStep() {

    }
}
