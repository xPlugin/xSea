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
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.enchant.CustomEnchantment;
import pr.lofe.mdr.xsea.xSea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.bukkit.Material.AIR;

public class SeaCommand extends Command {

    public SeaCommand() {
        super("xsea");
        src.withSubcommands(
                new Command("git") {
                    @Override
                    void execute(CommandSender sender, CommandArguments args) {
                        String arg = args.getRaw("arg");
                        assert arg != null;
                        switch (arg) {
                            case "version" -> {
                                InputStream in = xSea.I.getResource("version.txt");
                                if (in == null) {
                                    sender.sendMessage(TextWrapper.text("Невозможно получить версию. Плагин забилжен на коленках у араба."));
                                    return;
                                }

                                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                try {
                                    String commit = reader.readLine();
                                    if(commit == null) throw new IOException();

                                    sender.sendMessage(TextWrapper.text(String.format(
                                            "Версия плагина... Commit: <blue><click:open_url:'https://github.com/justlofe/xSea/commit/%s'>%s</click></blue>",
                                            commit,
                                            commit
                                    )));
                                } catch (IOException ignored) {
                                    sender.sendMessage(TextWrapper.text("Невозможно получить версию. Плагин забилжен на коленках у араба."));
                                }
                            }
                            case "url" -> {
                                sender.sendMessage(TextWrapper.text(
                                        "Repository URL: <blue><click:open_url:'https://github.com/justlofe/xGartic/'>[GitHub]</click></blue>"
                                ));
                            }
                            default -> {}
                        }
                    }
                }.src.withArguments(new TextArgument("arg").replaceSuggestions(ArgumentSuggestions.strings("version", "url"))),

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

                new Command("enchant") {
                    @Override
                    void execute(CommandSender sender, CommandArguments args) {
                        if(sender.hasPermission("*")) {
                            Player player = (Player) args.get("player");
                            assert player != null;
                            ItemStack item = player.getInventory().getItemInMainHand();
                            if(item.getType() != AIR) {
                                int level = 1;
                                if(args.get("level") instanceof Integer integer) level = integer;

                                xSea.WATER_RESISTANCE.enchant(item, level, CustomEnchantment.GlintMethod.GlintOverride);
                                player.sendMessage("Зачарование применено");
                            }
                        }
                    }
                }.src.withArguments(new PlayerArgument("player")).withOptionalArguments(new IntegerArgument("level", 1)),

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
                        if(sender.hasPermission("*")) {
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
                    }
                }.src
                        .withArguments(new PlayerArgument("player"), new TextArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> xSea.getItems().itemsIDs().toArray(new String[0]))))
                        .withOptionalArguments(new IntegerArgument("amount", 1, 64))
        );
    }

    @Override protected void execute(CommandSender commandSender, CommandArguments commandArguments) {}

}
