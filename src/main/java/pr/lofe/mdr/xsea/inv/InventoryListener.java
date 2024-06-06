package pr.lofe.mdr.xsea.inv;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.mechanics.Mechanic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pr.lofe.mdr.xsea.xSea;

import java.util.ArrayList;
import java.util.List;

public class InventoryListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inv = event.getView().getTopInventory();
        if(inv.getHolder() instanceof TableHolder) {
            for(int i : event.getRawSlots()) {
                switch (i) {
                    case 0, 3, 4, 5, 6, 7, 8, 9, 13, 14, 15, 16, 17, 18, 19, 21, 22, 23, 24, 25, 26 -> event.setCancelled(true);
                    default -> {}
                }
            }
        }
    }

    @EventHandler public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getView().getTopInventory();
        if(inv.getHolder() instanceof TableHolder holder) {
            int slotI = event.getSlot();
            ItemStack slot = event.getCurrentItem();
            Inventory bottom = event.getView().getBottomInventory();

            if(event.getClickedInventory() == inv) {
                switch (slotI) {
                    case 1, 2, 10, 11, 12, 20 -> {}
                    case 15 -> {
                        if(slot == null) event.setCancelled(true);
                        else {
                            for(int i: new int[]{1, 2, 10, 11, 12, 20}) {
                                ItemStack item = inv.getItem(i);
                                if(item != null) item.setAmount(item.getAmount() - 1);
                                Player player = (Player) event.getWhoClicked();
                                player.getWorld().playSound(player, "custom.sfx.carpenter_table_work", SoundCategory.BLOCKS, .5f,1f);
                            }
                        }
                    }
                    default -> event.setCancelled(true);
                }
            }
            else if(slotI != -999) {
                ItemStack curr = bottom.getItem(slotI);
                if(event.isShiftClick()) {
                    if(slot != null && curr != null) event.setCancelled(true);
                }
            }

            Bukkit.getScheduler().runTaskLater(xSea.I, () -> holder.update(inv), 1L);
        }
    }

    @EventHandler public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if(inv.getHolder() instanceof TableHolder holder) {
            holder.dropItems(player,
                    inv.getItem(1),
                    inv.getItem(2),
                    inv.getItem(10),
                    inv.getItem(11),
                    inv.getItem(12),
                    inv.getItem(20)
            );
        }
    }

    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null) {
            Mechanic mechanic = OraxenBlocks.getOraxenBlock(block.getLocation());
            if(mechanic != null) {
                if(mechanic.getItemID().equals("carpenter_table")) {
                    event.getPlayer().openInventory(new TableHolder().getInventory());
                    event.setCancelled(true);
                }
            }
        }
    }

}
