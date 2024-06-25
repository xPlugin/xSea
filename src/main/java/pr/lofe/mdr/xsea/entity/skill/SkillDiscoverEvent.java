package pr.lofe.mdr.xsea.entity.skill;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class SkillDiscoverEvent extends PlayerEvent {

    private final static HandlerList handlers = new HandlerList();

    private final Skill skill;

    public SkillDiscoverEvent(Player player, Skill skill) {
        super(player);
        this.skill = skill;
    }

    public Skill getSkill() {
        return skill;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}