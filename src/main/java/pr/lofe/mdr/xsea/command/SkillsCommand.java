package pr.lofe.mdr.xsea.command;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pr.lofe.mdr.xsea.entity.skill.SkillsHolder;

public class SkillsCommand extends Command{
    public SkillsCommand() {
        super("skills");
    }

    @Override
    void execute(CommandSender sender, CommandArguments args) {
        if(sender instanceof Player player) {
            Inventory inv = new SkillsHolder().getInventory();
            SkillsHolder.modify(inv, player);
            player.openInventory(inv);
        }
    }

}
