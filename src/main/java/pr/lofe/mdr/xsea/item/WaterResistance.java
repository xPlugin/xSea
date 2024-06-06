package pr.lofe.mdr.xsea.item;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

public class WaterResistance extends DigDurabilityEnchantment {

    public WaterResistance() {
        super(Enchantment.definition(
                ItemTags.DURABILITY_ENCHANTABLE,
                5,
                3,
                Enchantment.dynamicCost(5, 8),
                Enchantment.dynamicCost(55, 8),
                2,
                EquipmentSlot.MAINHAND));
    }

}
