package pr.lofe.mdr.xsea.inv;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.xSea;

import java.util.ArrayList;
import java.util.List;

public class TableHolder implements InventoryHolder {

    private final ItemStack empty = new ItemStack(Material.PAPER){{
        addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemMeta meta = getItemMeta();
        meta.displayName(TextWrapper.text(""));
        meta.setCustomModelData(1002);
        setItemMeta(meta);
    }};;

    @Override public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, TextWrapper.text("<white>ꐮꑽ</white>"));
        for (int i = 0; i < 27; i++) {
            switch (i) {
                case 0, 3, 4, 5, 6, 7, 8, 9, 13, 14, 16, 17, 18, 19, 21, 22, 23, 24, 25, 26 -> inv.setItem(i, empty);
                default -> {}
            }
        }
        return inv;
    }

    public void update(Inventory inv) {
        ItemStack nails = inv.getItem(1);
        if(nails == null || nails.getType() != Material.IRON_NUGGET) return;

        ItemStack[] matrix = {
                inv.getItem(2),
                inv.getItem(10), inv.getItem(11), inv.getItem(12),
                inv.getItem(20)
        };
        CarpenterRecipe recipe = xSea.getRecipes().getRecipe(matrix);
        if(recipe != null) {
            inv.setItem(15, recipe.getResult());
        }
        else inv.setItem(15, null);
    }

    private boolean isEmpty(ItemStack item) {
        if(item == null) return false;
        ItemMeta meta = item.getItemMeta();
        return(meta.hasCustomModelData() && meta.getCustomModelData() == empty.getItemMeta().getCustomModelData());
    }

    public void dropItems(Player player, ItemStack... items) {
        List<ItemStack> remained = new ArrayList<>();
        for(ItemStack item : items)
            if(item != null && !isEmpty(item)) remained.addAll(player.getInventory().addItem(item).values());

        Location eyes = player.getEyeLocation();
        Vector motion = eyes.getDirection().multiply(0.2);
        for(ItemStack item : remained) {
            Item ent = player.getWorld().dropItem(eyes, item);
            ent.setVelocity(motion);
        }
    }

}
