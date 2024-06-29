package pr.lofe.mdr.xsea.listener;

import com.google.common.collect.Lists;
import io.th0rgal.oraxen.utils.drops.Loot;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import pr.lofe.mdr.xsea.entity.level.PlayerLevel;
import pr.lofe.mdr.xsea.entity.skill.SkillRegistry;
import pr.lofe.mdr.xsea.util.MiningSkillUtil;
import pr.lofe.mdr.xsea.util.RandomUtil;
import pr.lofe.mdr.xsea.xSea;

import java.util.List;

public class BlockListener implements Listener {

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        List<NamespacedKey> allowed = Lists.newArrayList(
                LootTables.SHIPWRECK_TREASURE.getKey(),
                LootTables.SHIPWRECK_SUPPLY.getKey(),
                LootTables.BURIED_TREASURE.getKey(),
                LootTables.ABANDONED_MINESHAFT.getKey()
        );
        if(allowed.contains(event.getLootTable().getKey())) {
            if(RandomUtil.nextBool(30)) event.getLoot().add(PlayerLevel.generateBooster());
        }

        if(LootTables.SHIPWRECK_SUPPLY.getKey().equals(event.getLootTable().getKey())) {
            if(RandomUtil.nextBool(70)) event.getLoot().add(new ItemStack(Material.SUGAR_CANE, RandomUtil.nextInt(3)));
        }

        if(LootTables.RUINED_PORTAL.getKey().equals(event.getLootTable().getKey())) {
            if(RandomUtil.nextBool(20)) event.getLoot().add(new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
        }

        Entity ent = event.getEntity();
        if(ent instanceof Player player) {
            if(SkillRegistry.doesPlayerHasSkill(player, NamespacedKey.minecraft("adventurer_1"))) {
                List<ItemStack> loot = event.getLoot();
                for (int i = 0; i < loot.size(); i++) {
                    if(RandomUtil.nextBool(30)) {
                        ItemStack item = loot.get(i);
                        if(item.getItemMeta().hasMaxStackSize() && item.getItemMeta().getMaxStackSize() == 64 && item.getType() != Material.HEART_OF_THE_SEA) {
                            item.setAmount(Math.min(item.getAmount() + RandomUtil.nextInt(2), 64));
                            loot.set(i, item);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR) public void onBlockBreak(BlockBreakEvent event) {
        if(!event.isCancelled()) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            if(block.getType() == Material.GRAVEL) {
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
                    event.setDropItems(false);
                }
            }
            else if (block.getType().name().contains("_ORE") && SkillRegistry.doesPlayerHasSkill(player, NamespacedKey.minecraft("miner_7"))) {

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
