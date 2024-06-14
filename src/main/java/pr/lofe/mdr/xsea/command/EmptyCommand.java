package pr.lofe.mdr.xsea.command;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

public class EmptyCommand extends Command{

    public EmptyCommand(String cmd) {
        super(cmd);
    }

    @Override void execute(CommandSender sender, CommandArguments args) {}
}
