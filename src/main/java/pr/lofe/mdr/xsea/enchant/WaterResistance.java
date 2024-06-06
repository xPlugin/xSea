package pr.lofe.mdr.xsea.enchant;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import pr.lofe.mdr.xsea.util.RandomUtil;

public class WaterResistance extends CustomEnchantment<PlayerItemDamageEvent>
{
    public WaterResistance() {
        super(
                NamespacedKey.minecraft("water_resistance"),
                5,
                3,
                new Cost(5, 8),
                new Cost(55, 8),
                2
        );
    }

    @EventHandler
    @Override
    void effect(PlayerItemDamageEvent event) {
        System.out.println("event called");

        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        Block block = player.getTargetBlock(null, 5);
        if(block.getType() == Material.WATER || player.getEyeLocation().getBlock().getType() == Material.WATER) {
            int enchantLevel = getEnchantLevel(item, this);
            int damage = event.getDamage();

            if(enchantLevel == -1) {
                if(RandomUtil.nextBool(80d)) damage *= 2;
                else if(RandomUtil.nextBool(30d)) damage *= 3;
            }
            else if(RandomUtil.nextBool(20 * enchantLevel)){
                event.setCancelled(true);
                return;
            }

            if(damage != event.getDamage()) {
                event.setDamage(damage);
            }
        }
    }

}
