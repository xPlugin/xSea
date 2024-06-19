package pr.lofe.mdr.xsea.listener;

import com.google.common.collect.Lists;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTables;
import pr.lofe.mdr.xsea.entity.level.PlayerLevel;
import pr.lofe.mdr.xsea.util.RandomUtil;
import pr.lofe.mdr.xsea.xSea;

import java.util.List;

public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR) public void onBlockBreak(BlockBreakEvent event) {
        if(!event.isCancelled()) {
            Block block = event.getBlock();
            if(block.getType() == Material.GRAVEL) {
                Player player = event.getPlayer();

                ItemStack item = player.getActiveItem();
                double chance;
                switch (item.getEnchantmentLevel(Enchantment.FORTUNE)) {
                    case 3 -> chance = 17;
                    case 2 -> chance = 14;
                    case 1 -> chance = 9;
                    default -> chance = 5;
                }

                if(RandomUtil.nextBool(chance)) {
                    block.getWorld().dropItemNaturally(
                            block.getLocation().add(.5, .2, .5),
                            xSea.getItems().getItem("raw_titanium")
                    );
                }
            }
        }
    }

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
