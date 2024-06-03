package pr.lofe.mdr.xsea.command;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import pr.lofe.lib.xbase.cmd.Command;
import pr.lofe.mdr.xsea.xSea;

public class SeaCommand extends Command {

    public SeaCommand() {
        super("xsea", xSea.I);
        src.withSubcommands(
                new Command("give") {
                    @Override
                    protected void execute(CommandSender commandSender, CommandArguments commandArguments) {

                    }
                }.src
        );
        register();
    }

    @Override protected void execute(CommandSender commandSender, CommandArguments commandArguments) {}

}
