package pr.lofe.mdr.xsea.registry;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemRegistry {

    public static final @NotNull NamespacedKey pluginID = NamespacedKey.fromString("oraxen:id");

    private final Config data;
    private final HashMap<String, ItemStack> items = new HashMap<>();

    public ItemRegistry() {
        data = new Config("items", true, false);
    }

    public void init() {
        items.clear();
        for(String str: data.getConfig().getKeys(false)) {
            ConfigurationSection rawItem = data.getConfig().getConfigurationSection(str);
            assert rawItem != null;
            Material type = Material.valueOf(rawItem.getString("type", "air").toUpperCase());

            ItemStack item = new ItemStack(type);
            int customModelData = rawItem.getInt("cmd", -1);
            String rawName = rawItem.getString("display.name", "");
            String rawPluginID = rawItem.getString("oraxen", "");
            String rawColor = rawItem.getString("color", "");

            List<String>
                    flags = rawItem.getStringList("flags"),
                    lore  = rawItem.getStringList("display.lore"),
                    enchants = rawItem.getStringList("enchantments");

            ItemMeta meta = item.getItemMeta();

            if(!rawColor.isEmpty()) {
                Color color = hexToColor(rawColor);
                if(color != null) {
                    if(meta instanceof LeatherArmorMeta lMeta) lMeta.setColor(color);
                    else if(meta instanceof PotionMeta pMeta) pMeta.setColor(color);
                }
            }

            for(String raw: enchants) {
                String[] data = raw.split(":", 2);
                Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(data[0]));
                if(StringUtils.isNumeric(data[1]) && ench != null) {
                    meta.addEnchant(ench, Integer.parseInt(data[1]), true);
                }
            }

            if(customModelData != -1) meta.setCustomModelData(customModelData);
            meta.displayName(textWithLanguage(rawName));
            for(String rawFlag: flags) {
                ItemFlag flag = ItemFlag.valueOf(rawFlag.toUpperCase());
                if(flag == ItemFlag.HIDE_ENCHANTS) meta.setEnchantmentGlintOverride(false);
                meta.addItemFlags(flag);
            }

            if(!rawPluginID.isEmpty()) meta.getPersistentDataContainer().set(pluginID, PersistentDataType.STRING, rawPluginID);

            List<Component> finalLore = new ArrayList<>();
            for(String row: lore) finalLore.add(textWithLanguage(row));
            meta.lore(finalLore);

            item.setItemMeta(meta);

            items.put(str, item);
        }
    }

    public ItemStack getItem(String string) {
        return items.get(string).clone();
    }
    public ItemStack getItemByCMD(int customModelData) {
        for(ItemStack stack: items.values()) {
            if(stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == customModelData) return stack;
        }
        return null;
    }
    public String getKey(ItemStack item) {
        if (item == null) return "NULL";
        for(String str: items.keySet()) {
            ItemStack temp = items.get(str);
            if(temp.getType() == item.getType()) {
                if(item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == temp.getItemMeta().getCustomModelData()) {
                    return str;
                }
            }
        }
        return "NULL";
    }

    public List<String> itemsIDs() {
        return new ArrayList<>(items.keySet());
    }

    private static Component textWithLanguage(String input) {
        if(input.startsWith("&lang:")) return Component.translatable(input.replaceFirst("&lang:", "")).decoration(TextDecoration.ITALIC, false);
        return TextWrapper.text(input).decoration(TextDecoration.ITALIC, false);
    }

    public static Color hexToColor(String hex) {
        hex = hex.replace("#", "");
        return switch (hex.length()) {
            case 6 -> Color.fromRGB(
                    Integer.valueOf(hex.substring(0, 2), 16),
                    Integer.valueOf(hex.substring(2, 4), 16),
                    Integer.valueOf(hex.substring(4, 6), 16));
            case 8 -> Color.fromARGB(
                    Integer.valueOf(hex.substring(6, 8), 16),
                    Integer.valueOf(hex.substring(0, 2), 16),
                    Integer.valueOf(hex.substring(2, 4), 16),
                    Integer.valueOf(hex.substring(4, 6), 16));
            default -> null;
        };
    }

}
