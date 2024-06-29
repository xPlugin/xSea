package pr.lofe.mdr.xsea.command;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.debug.DebugMode;

public class DebugCommand extends Command{

    public DebugCommand() {
        super("отладка");
        src.withArguments(new TextArgument("toggle").replaceSuggestions(ArgumentSuggestions.strings("вкл", "выкл", "инфо")));
    }

    @Override
    void execute(CommandSender sender, CommandArguments args) {
        if(sender instanceof Player player) {
            String toggle = args.getRaw("toggle");
            assert toggle != null;

            switch (toggle) {
                case "вкл" -> {
                    DebugMode.setEnabled(player, true);
                    player.sendMessage(TextWrapper.text("Debug mode <green><u>enabled</u></green>!"));
                }
                case "выкл" -> {
                    DebugMode.setEnabled(player, false);
                    player.sendMessage(TextWrapper.text("Debug mode <red><u>disabled</u></red>."));
                }
                case "инфо" -> player.sendMessage(TextWrapper.text("Debug режим - позволяет отслеживать перезагрузки плагина <u>[xSea]</u>, и отслеживать изменения если у вас есть права оператора."));
                default -> {
                    Component output = TextWrapper.text("<red><lang:command.unknown.command><br><gray>debug <red><u>" + toggle + "<italic><lang:command.context.here>");
                    player.sendMessage(output);
                }
            }
        }
    }

}
