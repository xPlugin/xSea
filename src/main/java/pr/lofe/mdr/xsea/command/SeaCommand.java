package pr.lofe.mdr.xsea.command;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.enchant.CustomEnchantment;
import pr.lofe.mdr.xsea.entity.DisplayUpdate;
import pr.lofe.mdr.xsea.entity.FoodSystem;
import pr.lofe.mdr.xsea.entity.PlayerDifficulty;
import pr.lofe.mdr.xsea.entity.level.PlayerLevel;
import pr.lofe.mdr.xsea.entity.skill.SkillsHolder;
import pr.lofe.mdr.xsea.start.CamPath;
import pr.lofe.mdr.xsea.start.DifficultyHolder;
import pr.lofe.mdr.xsea.start.ResourcePackHolder;
import pr.lofe.mdr.xsea.start.StartEngine;
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

                new Command("update") {
                    @Override
                    void execute(CommandSender sender, CommandArguments args) {
                        DisplayUpdate.update();
                        sender.sendMessage("Обновление задач успешно.");
                    }
                }.src,

                new Command("reset-data") {
                    @Override
                    void execute(CommandSender sender, CommandArguments args) {
                        Player player = (Player) args.get("player");
                        if(player != null) {
                            Config data = xSea.data;
                            data.getConfig().set(player.getName() + ".difficulty", null);
                            data.getConfig().set(player.getName() + ".isCompletedStart", null);
                            data.save();
                            player.kick(TextWrapper.text("Your ocean data has been reset."));
                            sender.sendMessage("Сброшены игровые данные для игрока " + player.getName() + ".");
                        }
                    }
                }.src.withArguments(new PlayerArgument("player")),

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
                            case "url" -> sender.sendMessage(TextWrapper.text(
                                    "Repository URL: <blue><click:open_url:'https://github.com/justlofe/xSea/'>[GitHub]</click></blue>"
                            ));
                            default -> {}
                        }
                    }
                }.src.withArguments(new TextArgument("arg").replaceSuggestions(ArgumentSuggestions.strings("version", "url"))),

                new Command("reload") {
                    @Override
                    protected void execute(CommandSender sender, CommandArguments args) {
                        sender.sendMessage("Reloading... If next message is not appears, check console.");
                        xSea.I.reloadData();
                        sender.sendMessage("Reloaded!");
                    }
                }.src.withPermission("*"),

                new Command("enchant") {
                    @Override
                    void execute(CommandSender sender, CommandArguments args) {
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
                }.src.withArguments(new PlayerArgument("player")).withOptionalArguments(new IntegerArgument("level", 1)),

                new Command("test") {
                    @Override
                    void execute(CommandSender sender, CommandArguments args) {
                        if(sender instanceof Player player) {
                            player.showDemoScreen();
                        }
                    }
                }.src,

                new EmptyCommand("animation")
                        .src.withSubcommands(

                                new Command("play") {
                                    @Override
                                    void execute(CommandSender sender, CommandArguments args) {
                                        if(sender instanceof Player player) {
                                            StartEngine.animPart(player, "preview");
                                        }
                                    }
                                }.src,

                                new Command("setup") {
                                    @Override
                                    void execute(CommandSender sender, CommandArguments args) {
                                        if(sender instanceof Player player) {
                                            String pos = args.getRaw("position");
                                            assert pos != null;
                                            if(pos.equals("first")) AnimationHolder.first = player.getLocation();
                                            else if(pos.equals("second")) AnimationHolder.second = player.getLocation();
                                            player.sendMessage(TextWrapper.text("Установлена " + pos + " точка для проигрывания анимации."));
                                        }
                                    }
                                }.src.withArguments(new TextArgument("position").replaceSuggestions(ArgumentSuggestions.strings("first", "second"))),

                                new Command("playback") {
                                    @Override
                                    void execute(CommandSender sender, CommandArguments args) {
                                        if (sender instanceof Player player) {
                                            int time = (int) args.get("seconds");
                                            Location first = AnimationHolder.first, second = AnimationHolder.second;
                                            if (first == null || second == null) {
                                                player.sendMessage(TextWrapper.text("Одна из позиций не установлена."));
                                                return;
                                            }
                                            CamPath path = new CamPath(first, second, time);
                                            path.generatePath();
                                            path.runPath(player);
                                        }
                                    }
                                }.src.withArguments(new IntegerArgument("seconds"))
                        ),

                new Command("give") {
                    @Override
                    protected void execute(CommandSender sender, CommandArguments args) {
                        Player player = (Player) args.get("player");
                        assert player != null;

                        String id = args.getRaw("item");
                        assert id != null;

                        ItemStack item;
                        if(id.equals("random_level_booster")) item = PlayerLevel.generateBooster();
                        else item = xSea.getItems().getItem(id);

                        if(item != null) {
                            int amount = 1;
                            if(args.get("amount") instanceof Integer integer) amount = integer;
                            item.setAmount(amount);

                            player.getInventory().addItem(item);
                            sender.sendMessage(
                                    Component.text("Выдано "+ (amount == 1 ? "" : (amount) + " ")).append(item.displayName()).append(Component.text(" игроку " + player.getName()))
                            );
                        }
                    }
                }.src
                        .withArguments(new PlayerArgument("player"), new TextArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> xSea.getItems().itemsIDs().toArray(new String[0]))))
                        .withOptionalArguments(new IntegerArgument("amount", 1, 64)),



                new EmptyCommand("player").src.withSubcommands(
                        new Command("difficulty") {
                            @Override
                            void execute(CommandSender sender, CommandArguments args) {
                                Player player = (Player) args.get("player");
                                String action = args.getRaw("get-or-set");
                                assert action != null && player != null;

                                if(action.equals("get")) {
                                    PlayerDifficulty diff = PlayerDifficulty.getDifficulty(player);
                                    sender.sendMessage(TextWrapper.text("Сложность у игрока " + player.getName() + " установлена на " + diff.name()));
                                }
                                else if (action.equals("set")) {
                                    String newDiff = args.getRaw("new-difficulty");
                                    if(newDiff == null) {
                                        sender.sendMessage("Укажите новую сложность!");
                                        return;
                                    }
                                    PlayerDifficulty diff = PlayerDifficulty.valueOf(newDiff);
                                    PlayerDifficulty.setDifficulty(player, diff);
                                    sender.sendMessage(TextWrapper.text("Сложность у игрока " + player.getName() + " теперь равна " + diff.name()));
                                }
                            }
                        }.src
                                .withArguments(new PlayerArgument("player"), new TextArgument("get-or-set").replaceSuggestions(ArgumentSuggestions.strings("get", "set")))
                                .withOptionalArguments(new TextArgument("new-difficulty").replaceSuggestions(ArgumentSuggestions.strings("EASY", "HARD"))),

                        new Command("food") {
                            @Override
                            void execute(CommandSender sender, CommandArguments args) {
                                Player player = (Player) args.get("player");
                                String action = args.getRaw("action");
                                assert action != null && player != null;

                                if(action.equals("getIndexState")) {
                                    FoodSystem.FoodIndexState state = FoodSystem.getIndexState(player);
                                    String output = switch (state){
                                        case IDEAL -> "<gradient:yellow:gold>идеально</gradient>.";
                                        case GOOD -> "<gradient:#ffe9e6:#ff9999>правильно</gradient>.";
                                        case POOR -> "<gradient:#edfffa:#323634>ужасно</gradient>...";
                                    };
                                    sender.sendMessage(TextWrapper.text("Игрок " + player.getName() + " питается <bold>" + output));
                                }
                            }
                        }.src.withArguments(new PlayerArgument("player"), new TextArgument("action").replaceSuggestions(ArgumentSuggestions.strings("getIndexState")))
                ),


                new Command("gui") {
                    @Override
                    void execute(CommandSender sender, CommandArguments args) {
                        String raw = args.getRaw("type");
                        assert raw != null;

                        Player player;
                        if(args.get("who") instanceof Player player1) player = player1;
                        else if(sender instanceof Player player1) player = player1;
                        else {
                            sender.sendMessage("Команду должен выполнять игрок или игрок должен быть указан!");
                            return;
                        }

                        Inventory inv = null;
                        switch (raw) {
                            case "difficulty_choose" -> inv = new DifficultyHolder().getInventory();
                            case "resourcepack_done" -> inv = new ResourcePackHolder().getInventory();
                            case "skills" -> {
                                inv = new SkillsHolder().getInventory();
                                SkillsHolder.modify(inv, player);
                            }
                            default -> {}
                        }

                        if(inv != null) player.openInventory(inv);
                    }
                }.src.withArguments(new TextArgument("type").replaceSuggestions(ArgumentSuggestions.strings("difficulty_choose", "resourcepack_done", "skills"))).withOptionalArguments(new PlayerArgument("who"))
        ).withPermission("*");
    }

    @Override protected void execute(CommandSender commandSender, CommandArguments commandArguments) {}

    public static class AnimationHolder {
        public static Location first, second;
    }

}
