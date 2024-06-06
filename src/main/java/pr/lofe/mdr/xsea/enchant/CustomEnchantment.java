package pr.lofe.mdr.xsea.enchant;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomEnchantment<IEvent extends Event> implements Listener {

    private static final NamespacedKey ENCH = NamespacedKey.minecraft("custom_enchant");

    private final NamespacedKey key;
    private final int maxLevel;
    private final int weight;
    private final Cost minCost, maxCost;
    private final int anvilCost;

    public CustomEnchantment(NamespacedKey key, int weight, int maxLevel, Cost min, Cost max, int anvilCost) {
        this.key = key;
        this.maxLevel = maxLevel;

        this.weight = weight;
        this.minCost = min;
        this.maxCost = max;
        this.anvilCost = anvilCost;
    }

    public NamespacedKey key() {
        return key;
    }
    public int getMaxLevel() {
        return maxLevel;
    }
    public Cost getMinCost() {
        return minCost;
    }
    public Cost getMaxCost() {
        return maxCost;
    }
    public int getAnvilCost() {
        return anvilCost;
    }
    public int getWeight() {
        return weight;
    }

    private String translationKey() {
        return "enchantment." + key.namespace() + "." + key.value();
    }

    public @NotNull Component displayName(int level) {
        Component component = Component.translatable(translationKey()).append(Component.text(" ")).decoration(TextDecoration.ITALIC, false);
        component = component.color(TextColor.color(170, 170, 170));

        if(level != 1 || maxLevel != 1) component = component.append(Component.translatable("enchantment.level." + level));
        return component;
    }

    @EventHandler abstract void effect(IEvent event);

    private static Enchantment byItemGroup(ItemGroup group) {
        return switch (group) {
            case SWORD, AXE, SHOVEL, PICKAXE, HOE, BOW, CROSSBOW -> Enchantment.PROTECTION;
            case HELMET, CHESTPLATE, LEGGINGS, BOOTS -> Enchantment.LUCK_OF_THE_SEA;
            default -> Enchantment.MENDING;
        };
    }

    public void enchant(ItemStack item, int level, GlintMethod glintMethod) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(getEnchantLevel(item, this) == level) return;
        else removeEnchant(item, this);

        data.set(ENCH, PersistentDataType.STRING, key.toString());
        data.set(key, PersistentDataType.INTEGER, level);

        List<Component> lore = meta.lore();
        if(lore == null) lore = new ArrayList<>();

        Component line = displayName(level);

        MiniMessage mm = MiniMessage.miniMessage();
        if(!lore.isEmpty()) {
            int i = lore.size() - 1;
            String str = mm.serialize(lore.get(i));
            if(str.contains(translationKey())) lore.set(i, line);
            else {
                lore.add(Component.text(""));
                lore.add(line);
            }
        }
        else lore.add(line);
        meta.lore(lore);

        switch (glintMethod) {
            case GlintOverride -> meta.setEnchantmentGlintOverride(true);
            case HiddenEnchantment -> {
                ItemGroup group = ItemGroup.byItem(item.getType());
                Preconditions.checkArgument(group != null, "");
                Enchantment enchant = byItemGroup(group);

                meta.addEnchant(enchant, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            default -> {}
        }

        item.setItemMeta(meta);
    }

    public static boolean hasEnchant(ItemStack item, CustomEnchantment enchant) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(!data.has(ENCH)) return false;
        String rawName = data.get(ENCH, PersistentDataType.STRING);
        if(rawName == null) return false;
        return rawName.equals(enchant.key.toString());
    }

    public static int getEnchantLevel(ItemStack item, CustomEnchantment enchant) {
        if(!hasEnchant(item, enchant)) return -1;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        NamespacedKey key = enchant.key;
        if(key == null) return -1;

        Integer level = data.get(key, PersistentDataType.INTEGER);
        if(level == null) return -1;

        return level;
    }

    public static void removeEnchant(ItemStack item, CustomEnchantment enchant) {
        if(!hasEnchant(item, enchant)) return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.remove(ENCH);
        data.remove(enchant.key);

        List<Component> lore = meta.lore();
        assert lore != null;
        if(lore.size() > 2) {
            lore.removeLast();
            lore.removeLast();
        }
        else lore.removeLast();
        meta.lore(lore);

        if(meta.hasEnchantmentGlintOverride()) meta.setEnchantmentGlintOverride(null);
        else {
            ItemGroup group = ItemGroup.byItem(item.getType());
            assert group != null;
            Enchantment hidden = byItemGroup(group);
            meta.removeEnchant(hidden);
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
    }

    public record Cost(int base, int perLevel) {
        public Cost(int base, int perLevel) {
            this.base = base;
            this.perLevel = perLevel;
        }

        public int calculate(int level) {
            return this.base + this.perLevel * (level - 1);
        }

        public int base() {
            return this.base;
        }

        public int perLevel() {
            return this.perLevel;
        }
    }

    public enum GlintMethod {
        /**
         * Item can have visible glint without anyone enchant, or have not visible glint with enchants. Recommended for mostly situations.
         */
        GlintOverride,

        /**
         * Adds a real one enchantment that does not affect type of item you enchant. This break vanilla enchants display.
         */
        HiddenEnchantment
    }

    public enum ItemGroup {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,

        SWORD,
        AXE,
        SHOVEL,
        PICKAXE,
        HOE,

        BOW,
        CROSSBOW,

        ROD;

        private final Material[] items;

        ItemGroup() {
            List<Material> types = new ArrayList<>();
            for(Material material: Material.values()) {
                if(material.name().contains(this.name())) types.add(material);
            }
            items = types.toArray(new Material[0]);
        }

        public boolean includes(Material type) {
            for (Material mat: items) if(mat == type) return true;
            return false;
        }

        public static ItemGroup byItem(Material type) {
            for(ItemGroup group: values()) {
                if (group.includes(type)) return group;
            }
            return null;
        }
    }

}
