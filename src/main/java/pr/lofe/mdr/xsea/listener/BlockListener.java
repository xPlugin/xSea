package pr.lofe.mdr.xsea.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            assert block != null;
            if(block.getType() == Material.DIRT) {
                Block upwards = block.getRelative(0, 1, 0);
                if(upwards.getType() != Material.AIR) return;

                ItemStack item = event.getItem();
                if(item != null && item.getType() == Material.BONE_MEAL) {
                    event.setUseItemInHand(Event.Result.ALLOW);
                    block.setType(Material.GRASS_BLOCK);
                }
            }
        }
    }

    @EventHandler public void onPortalCreate(PortalCreateEvent event) {
        if(event.getReason() == PortalCreateEvent.CreateReason.FIRE) event.setCancelled(true);
    }

}
