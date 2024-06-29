package pr.lofe.mdr.xsea.command;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pr.lofe.mdr.xsea.start.DifficultyHolder;

public class DifficultyCommand extends Command{
    public DifficultyCommand() {
        super("сложность");
    }

    @Override
    void execute(CommandSender sender, CommandArguments args) {
        if(sender instanceof Player player) {
            player.openInventory(new DifficultyHolder().getInventory());
        }
    }

}
