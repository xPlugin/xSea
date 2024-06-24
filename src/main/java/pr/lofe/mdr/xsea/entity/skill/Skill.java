package pr.lofe.mdr.xsea.entity.skill;

import org.bukkit.NamespacedKey;

public abstract class Skill {

    public abstract boolean isInit();
    public abstract NamespacedKey key();
    public abstract String description();
    public abstract String name();

    public abstract int currency();

    public static Skill create(boolean isInit, NamespacedKey key, String description, String name, int currency) {
        return new Skill() {
            @Override
            public boolean isInit() {
                return isInit;
            }

            @Override
            public NamespacedKey key() {
                return key;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public int currency() {
                return currency;
            }
        };
    }

}
