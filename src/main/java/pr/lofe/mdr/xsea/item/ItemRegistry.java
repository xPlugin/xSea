package pr.lofe.mdr.xsea.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemRegistry {

    private static final NamespacedKey pluginID = NamespacedKey.fromString("oraxen:id");

    private final Config data;
    private final HashMap<String, ItemStack> items = new HashMap<>();

    public ItemRegistry() {
        data = new Config("items", true);
    }

    public void init() {
        items.clear();
        for(String str: data.getConfig().getKeys(false)) {
            System.out.println(str);
            ConfigurationSection rawItem = data.getConfig().getConfigurationSection(str);
            assert rawItem != null;
            Material type = Material.valueOf(rawItem.getString("type", "air").toUpperCase());

            ItemStack item = new ItemStack(type);
            int customModelData = rawItem.getInt("cmd", -1);
            String name = rawItem.getString("display.name", "");
            String pluginID = rawItem.getString("oraxen", "");

            List<String>
                    flags = rawItem.getStringList("flags"),
                    lore  = rawItem.getStringList("display.lore");

            item.editMeta(meta -> {
               if(customModelData != -1) meta.setCustomModelData(customModelData);
               meta.displayName(textWithLanguage(name));
               for(String flag: flags) {
                   meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
               }

               if(!pluginID.isEmpty()) meta.getPersistentDataContainer().set(this.pluginID, PersistentDataType.STRING, pluginID);

               List<Component> finalLore = new ArrayList<>();
               for(String row: lore) finalLore.add(textWithLanguage(row));
               meta.lore(finalLore);
            });
            items.put(str, item);
        }
    }

    public ItemStack getItem(String string) {
        return items.get(string);
    }
    public ItemStack getItemByCMD(int customModelData) {
        for(ItemStack stack: items.values()) {
            if(stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == customModelData) return stack;
        }
        return null;
    }

    public List<String> itemsIDs() {
        return new ArrayList<>(items.keySet());
    }

    private static Component textWithLanguage(String input) {
        if(input.startsWith("&lang:")) return Component.translatable(input.replaceFirst("&lang:", "")).decoration(TextDecoration.ITALIC, false);
        return TextWrapper.text(input).decoration(TextDecoration.ITALIC, false);
    }

}
