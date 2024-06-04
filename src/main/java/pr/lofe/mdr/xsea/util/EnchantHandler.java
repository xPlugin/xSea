package pr.lofe.mdr.xsea.util;

import net.minecraft.core.registries.BuiltInRegistries;

import java.util.IdentityHashMap;

public class EnchantHandler {

    public static void unfreezeRegistry() {
        Reflex.setFieldValue(BuiltInRegistries.ENCHANTMENT, "l", false);
        Reflex.setFieldValue(BuiltInRegistries.ENCHANTMENT, "m", new IdentityHashMap<>());
    }

    public static void freezeRegistry() {
        BuiltInRegistries.ENCHANTMENT.freeze();
    }

}
