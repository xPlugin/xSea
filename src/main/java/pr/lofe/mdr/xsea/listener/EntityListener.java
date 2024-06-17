package pr.lofe.mdr.xsea.listener;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.entity.PlayerDifficulty;
import pr.lofe.mdr.xsea.util.RandomUtil;
import pr.lofe.mdr.xsea.xSea;

import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EntityListener implements Listener {

    private final HashMap<Player, Integer> airTick = new HashMap<>();

    @EventHandler public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player player) {
            PlayerDifficulty diff = PlayerDifficulty.getDifficulty(player);
            if(event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                if(diff == PlayerDifficulty.EASY && RandomUtil.nextBool(10)) event.setCancelled(true);
            }
            else if (diff == PlayerDifficulty.HARD) event.setDamage(event.getDamage() * 1.15);
        }
    }

    @EventHandler public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if(PlayerDifficulty.getDifficulty(player) == PlayerDifficulty.HARD) {
            for(ItemStack item: event.getDrops()) {
                if(RandomUtil.nextBool(.8)) {
                    event.getDrops().remove(item);
                    break;
                }
            }
        }
    }

    @EventHandler public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player player) {
            PlayerDifficulty diff = PlayerDifficulty.getDifficulty(player);
            if(diff == PlayerDifficulty.EASY) {
                if (event.getFoodLevel() > player.getFoodLevel() && RandomUtil.nextBool(60)) event.setFoodLevel(Math.min(event.getFoodLevel() + 1, 20));
            }
            else {
                // TODO
            }
        }
    }

    @EventHandler public void onPlayerExpChange(PlayerExpChangeEvent event) {
        if(PlayerDifficulty.getDifficulty(event.getPlayer()) == PlayerDifficulty.EASY) event.setAmount((int) (event.getAmount() * 0.9));
    }

    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().name().contains("RIGHT_CLICK")) {

            if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                assert block != null;

                if(block.getType().isInteractable() && !player.isSneaking()) return;
            }

            ItemStack hand = player.getInventory().getItemInMainHand();
            ItemStack slot = player.getInventory().getItem(9);

            String handID = xSea.getItems().getKey(hand);
            if(handID == null || !handID.contains("oxygen_tank")) return;

            String slotID = xSea.getItems().getKey(slot);
            if(slotID == null || !slotID.contains("oxygen_tank")) {
                player.getInventory().setItem(9, hand);
                player.getInventory().setItemInMainHand(null);
                player.playSound(player, Sound.ITEM_ARMOR_EQUIP_IRON, 1f, 1f);
            }
            else if(!handID.equals(slotID)) {
                player.getInventory().setItem(9, hand);
                player.getInventory().setItemInMainHand(slot);
                player.playSound(player, Sound.ITEM_ARMOR_EQUIP_IRON, 1f, 1f);
            }
        }
    }

    @EventHandler public void tick(ServerTickEndEvent event) {
        for(Player player: Bukkit.getOnlinePlayers()) {
            boolean inWater = playerInWater(player);
            if(inWater) {
                ItemStack item = player.getInventory().getBoots(); // getBoobs
                if(xSea.getItems().getKey(item).equals("flippers")) {
                    PotionEffect effect = player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE);
                    if(effect == null) effect = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 2, 0, true, false, false);
                    else if (!effect.hasIcon()) effect = effect.withDuration(2);

                    player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
                    player.addPotionEffect(effect);
                }
            }

            // if(player.getRemainingAir() > 0 && inWater) player.setRemainingAir(player.getRemainingAir() + 1);
            ItemStack balloon = player.getInventory().getItem(9);

            int balloonCapacity = 700;
            if(balloon != null) {
                String id = xSea.getItems().getKey(balloon);
                if("oxygen_tank_light".equals(id)) balloonCapacity = 1500;
                else if ("oxygen_tank".equals(id)) balloonCapacity = 2700;
            }

            airTick.putIfAbsent(player, balloonCapacity);
            int currentAmount = airTick.get(player);
            if(currentAmount > balloonCapacity) currentAmount = balloonCapacity;
            airTick.put(player, currentAmount);

            int bubbleVolume = balloonCapacity / 10;

            if(inWater && currentAmount >= 0) currentAmount--;
            else if(currentAmount < balloonCapacity) currentAmount += 3;
            airTick.put(player, currentAmount);

            int bubbles = (currentAmount / bubbleVolume);
            player.setRemainingAir(bubblesToTicks(bubbles) + 1);
        }
    }

    private static boolean playerInWater(Player player) {
        if(!player.isInBubbleColumn()) {
            Block block = player.getEyeLocation().getBlock();
            if(block.getType() == Material.WATER) return true;
            else if (block.getBlockData() instanceof Waterlogged waterlogged) return waterlogged.isWaterlogged();
        }
        return false;
    }

    private static int bubblesToTicks(int bubbles) {
        return 30 * (bubbles - 1) + 3;
    }

    public void damage() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            if(playerInWater(player)) {
                airTick.putIfAbsent(player, 700);
                int currentAmount = airTick.get(player);
                if(currentAmount <= 1) {
                    player.damage(1, DamageSource.builder(DamageType.DROWN).build());
                    float pitch = 0;
                    if(player.getHealth() < 5) pitch = .5f;
                    else if (player.getHealth() < 10) pitch = .7f;
                    else if (player.getHealth() < 15) pitch = 1f;
                    if(pitch > 0) player.playSound(player, "custom.sfx.heartbeat", 1, pitch);
                }
            }
        }
    }

}
