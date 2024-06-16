package pr.lofe.mdr.xsea.start;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pr.lofe.lib.xbase.text.TextWrapper;

public class ResourcePackHolder extends StartHolder {

    private final ItemStack empty = new ItemStack(Material.PAPER){{
        addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemMeta meta = getItemMeta();
        meta.displayName(TextWrapper.text(""));
        meta.setCustomModelData(1002);
        setItemMeta(meta);
    }};

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 36, "§fꐮꓓ");
        for (int i = 0; i < 36; i++) {
            switch (i) {
                case 19, 20, 21, 23, 24, 25 -> inv.setItem(i, empty);
                default -> {}
            }
        }
        return inv;
    }

}