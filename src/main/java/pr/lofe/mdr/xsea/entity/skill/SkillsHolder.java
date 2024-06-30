package pr.lofe.mdr.xsea.entity.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.xSea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillsHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 54, TextWrapper.text("<white>ꐮꓒ"));
    }

    public static void modify(Inventory inv, Player player) {
        for (int i = 0; i < 54; i++) {
            int index = i % 9;
            int row = (i - index) / 9;
            Skill skill = SkillRegistry.getSkill(row, index);
            if(skill != null) inv.setItem(i, generate(skill, player));
        }
    }

    public static ItemStack generate(Skill skill, Player player) {
        ItemStack item = xSea.getItems().getItem(skill.isInit() ? "skill_init" : "skill");
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        boolean has = SkillRegistry.doesPlayerHasSkill(player, skill.key()), canHas = SkillRegistry.canPlayerDiscoverSkill(player, skill.key());

        meta.setColor(
                has ? Color.fromRGB(193, 255, 122) : (canHas ? Color.fromRGB(255, 220, 122) : Color.fromRGB(255, 255, 255))
        );

        if(skill.currency() == 999) meta.setColor(Color.fromRGB(54, 54, 54));

        if(!has && canHas) meta.setEnchantmentGlintOverride(true);

        String rawName = skill.name();
        if(rawName == null) rawName = "втоᴘичный нᴀвык";
        meta.displayName(TextWrapper.text(rawName).decoration(TextDecoration.ITALIC, false));

        List<String> lines = new ArrayList<>();
        lines.add("");

        lines.addAll(Arrays.asList(skill.description().split("\n")));

        lines.add("");
        String currency = skill.currency() + "⌀";
        lines.add(has ? "<green>изʏчᴇно</green>" : (canHas ? "<green>" + currency : "<red>" + currency));
        meta.lore(lore(lines));
        item.setItemMeta(meta);

        return item;
    }

    private static List<Component> lore(List<String> lines) {
        List<Component> lore = new ArrayList<>();
        for(String line: lines) {
            lore.add(TextWrapper.text("<white>" + line + "<white>").decoration(TextDecoration.ITALIC, false));
        }
        return lore;
    }

}
