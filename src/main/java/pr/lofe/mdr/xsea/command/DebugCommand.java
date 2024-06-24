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
        super("debug");
        src.withArguments(new TextArgument("toggle").replaceSuggestions(ArgumentSuggestions.strings("on", "off", "info")));
    }

    @Override
    void execute(CommandSender sender, CommandArguments args) {
        if(sender instanceof Player player) {
            String toggle = args.getRaw("toggle");
            assert toggle != null;

            switch (toggle) {
                case "on" -> {
                    DebugMode.setEnabled(player, true);
                    player.sendMessage(TextWrapper.text("Debug mode <green><u>enabled</u></green>!"));
                }
                case "off" -> {
                    DebugMode.setEnabled(player, false);
                    player.sendMessage(TextWrapper.text("Debug mode <red><u>disabled</u></red>."));
                }
                case "info" -> {
                    player.sendMessage(TextWrapper.text("Debug режим - позволяет отслеживать перезагрузки плагина <u>[xSea]</u>, и отслеживать изменения если у вас есть права оператора. Так-же, можно отслеживать разные данные и калькуляции рандома связанные с вашим геймплеем."));
                }
                default -> {
                    Component output = TextWrapper.text("<red><lang:command.unknown.command><br><gray>debug <red><u>" + toggle + "<italic><lang:command.context.here>");
                    player.sendMessage(output);
                }
            }
        }
    }

}
