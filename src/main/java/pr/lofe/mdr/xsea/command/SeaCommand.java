package pr.lofe.mdr.xsea.command;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pr.lofe.mdr.xsea.xSea;

public class SeaCommand extends Command {

    public SeaCommand() {
        super("xsea");
        src.withSubcommands(
                new Command("reload") {
                    @Override
                    protected void execute(CommandSender sender, CommandArguments args) {
                        if(sender.hasPermission("*")) {
                            sender.sendMessage("Reloading... If next message is not appears, check console.");
                            xSea.I.reloadData();
                            sender.sendMessage("Reloaded!");
                        }
                    }
                }.src,

                new Command("test") {
                    @Override
                    void execute(CommandSender sender, CommandArguments args) {
                        String input = args.getRaw("input");
                        assert input != null;

                        MiniMessage mm = MiniMessage.miniMessage();
                        Component msg = Component.translatable(input);

                        Component result = msg.append(Component.text("\n" + mm.serialize(msg)));
                        sender.sendMessage(result);
                    }
                }.src.withArguments(new TextArgument("input")),

                new Command("give") {
                    @Override
                    protected void execute(CommandSender sender, CommandArguments args) {
                        Player player = (Player) args.get("player");
                        assert player != null;

                        String id = args.getRaw("item");
                        assert id != null;

                        ItemStack item = xSea.getItems().getItem(id);
                        if(item != null) {
                            int amount = 1;
                            if(args.get("amount") instanceof Integer integer) amount = integer;

                            player.getInventory().addItem(item);
                            sender.sendMessage(
                                    Component.text("Выдано "+ (amount == 1 ? "" : (amount) + " ")).append(item.displayName()).append(Component.text(" игроку " + player.getName()))
                            );
                        }
                    }
                }.src
                        .withArguments(new PlayerArgument("player"), new TextArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> xSea.getItems().itemsIDs().toArray(new String[0]))))
                        .withOptionalArguments(new IntegerArgument("amount", 1, 64))
        );
    }

    @Override protected void execute(CommandSender commandSender, CommandArguments commandArguments) {}

}
