package pr.lofe.mdr.xsea.listener;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.kyori.adventure.text.Component;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pr.lofe.mdr.xsea.xSea;

import java.util.HashMap;

public class EntityListener implements Listener {

    private final HashMap<Player, Integer> airTick = new HashMap<>();

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

            boolean inWater = false;

            if(!player.isInBubbleColumn()) {
                Block block = player.getEyeLocation().getBlock();
                if(block.getType() == Material.WATER) inWater = true;
                else if (block.getBlockData() instanceof Waterlogged waterlogged) inWater = waterlogged.isWaterlogged();
            }

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

    private static int bubblesToTicks(int bubbles) {
        return 30 * (bubbles - 1) + 3;
    }

    public void damage() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            if(player.isInWater()) {
                airTick.putIfAbsent(player, 700);
                int currentAmount = airTick.get(player);
                if(currentAmount <= 0) {
                    player.damage(1, DamageSource.builder(DamageType.DROWN).build());
                }
            }
        }
    }

}
