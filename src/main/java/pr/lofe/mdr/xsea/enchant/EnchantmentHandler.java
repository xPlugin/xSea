package pr.lofe.mdr.xsea.enchant;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import pr.lofe.mdr.xsea.util.RandomUtil;
import pr.lofe.mdr.xsea.xSea;

import java.util.Map;

public class EnchantmentHandler implements Listener {

    @EventHandler public void onPrepareResult(PrepareGrindstoneEvent event) {
        GrindstoneInventory grind = event.getInventory();

        ItemStack item1 = grind.getLowerItem(), item2 = grind.getUpperItem();

        if(item1 == null && item2 == null) return;

        Material type1 = type(item1), type2 = type(item2);
        if(type1 == Material.AIR || type2 == Material.AIR) {
            ItemStack item = item1 == null ? item2.clone() : item1.clone();
            if(CustomEnchantment.hasEnchant(item, xSea.WATER_RESISTANCE)) {
                CustomEnchantment.removeEnchant(item, xSea.WATER_RESISTANCE);
                event.setResult(item);
            }
        }
    }

    @EventHandler public void onEnchantItem(EnchantItemEvent event) {
        if(CustomEnchantment.ItemGroup.AFFECTED.includes(event.getItem().getType())) {
            Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
            for (Enchantment temp : enchants.keySet()) {
                if (temp == Enchantment.UNBREAKING) {
                    if(RandomUtil.nextBool(50)) {
                        int level = enchants.remove(temp);
                        Bukkit.getScheduler().runTaskLater(xSea.I, () -> xSea.WATER_RESISTANCE.enchant(event.getItem(), level, CustomEnchantment.GlintMethod.GlintOverride), 0L);
                    }
                }
            }
        }
    }


    // TODO
    // Не работает, переписать
    @EventHandler public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();

        WaterResistance WR = xSea.WATER_RESISTANCE;
        ItemStack first = inv.getFirstItem(), second = inv.getSecondItem();
        if (first == null) return;
        if (second == null) return;
        if(first.getType() != second.getType() && second.getType() != Material.ENCHANTED_BOOK) return;

        int fL = CustomEnchantment.getEnchantLevel(first, WR), sL = CustomEnchantment.getEnchantLevel(second, WR);
        if(fL >= 1 && second.getItemMeta().hasEnchant(Enchantment.UNBREAKING)) return;
        if(sL >= 1 && first.getItemMeta().hasEnchant(Enchantment.UNBREAKING)) return;
        if(fL == -1 && sL == -1) return;

        int newLevel;
        if(fL == sL && fL + 1 <= 3) newLevel = fL + 1;
        else newLevel = Math.max(fL, sL);

        if(newLevel > 0) {
            ItemStack item = event.getResult();
            if(item == null) item = first.clone();
            WR.enchant(item, newLevel, CustomEnchantment.GlintMethod.GlintOverride);
            event.setResult(item);
        }
    }


    private static Material type(ItemStack item) {
        if(item == null) return Material.AIR;
        return item.getType();
    }

}
