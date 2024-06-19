package pr.lofe.mdr.xsea.entity.skill;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import pr.lofe.lib.xbase.text.TextWrapper;

public class SkillsHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, TextWrapper.text("<white>ꐮꓒ"));
        return inv;
    }

}
