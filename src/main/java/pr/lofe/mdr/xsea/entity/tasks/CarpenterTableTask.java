package pr.lofe.mdr.xsea.entity.tasks;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pr.lofe.mdr.xsea.entity.PlayerTask;

public class CarpenterTableTask extends PlayerTask {

    @Override
    public @NotNull String displayName() {
        return "Создайте стол плотника";
    }

    @Override
    public NamespacedKey key() {
        return NamespacedKey.minecraft("carpenter_table");
    }

    @Override
    public int levelPoints() {
        return 3;
    }

    @Override public void completeEffects(Player player) {}

}
