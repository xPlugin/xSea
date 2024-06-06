package pr.lofe.mdr.xsea.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.jetbrains.annotations.NotNull;

public class WaterResistance extends DigDurabilityEnchantment {

    public WaterResistance(Rarity weight, EquipmentSlot... slotTypes) {
        super(weight, slotTypes);
    }

    @Override
    public @NotNull Component getFullname(int level) {
        MutableComponent mutableComponent = Component.translatable("enchantment.minecraft.water_resistance");

        if (level != 1 || this.getMaxLevel() != 1) {
            mutableComponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
        }

        return mutableComponent;
    }
}
