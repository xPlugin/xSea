package pr.lofe.mdr.xsea.entity;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pr.lofe.lib.xbase.text.TextWrapper;

public abstract class PlayerTask {

    public abstract @NotNull String displayName();
    public abstract NamespacedKey key();
    public abstract int levelPoints();

    public abstract void completeEffects(Player player);

    public void complete(Player player) {
        completeEffects(player);
        player.sendActionBar(TextWrapper.text("Вы завершли цель! Так держать!"));
        player.playSound(player, "entity.experience_orb.pickup", 1, 2);
        TasksRegistry.complete(this, player);
    }

    public void register() {
        TasksRegistry.add(this);
    }

}
