package pr.lofe.mdr.xsea.start;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pr.lofe.lib.xbase.text.TextWrapper;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class DifficultyHolder extends StartHolder {

    private final ItemStack empty = new ItemStack(Material.PAPER){{
        addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemMeta meta = getItemMeta();
        meta.displayName(TextWrapper.text(""));
        meta.setCustomModelData(1002);
        setItemMeta(meta);
    }};

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 36, "§fꐮ\uA4CF");
        // 12 13 14 3 4 5
        ItemStack pick = empty.clone();
        pick.editMeta(meta -> {
            meta.lore(lore(
                    "",
                    "<gray>|</gray> <white>Сложность игры позволяет выбрать</white>",
                    "<gray>|</gray> <white><color:#3477eb>индивидуальный</color> стиль игры, в котором</white>",
                    "<gray>|</gray> <white>определяются <green><u>минимальные изменения</u></green>.</white>",
                    " ",
                    "<gray>|</gray> <white>Выбирая большую сложнсть, вы получите</white>",
                    "<gray>|</gray> <white>больше преимущества как</white>",
                    "<gray>|</gray> <white>вознаграждение. Выбирайте <gold><u>с умом</u></gold>.</white>"
            ));
            meta.displayName(TextWrapper.text("<gold><u>Что такое сложность?</u></gold>").decoration(TextDecoration.ITALIC, false));
        });

        // 19 20 21, 23 24 25
        ItemStack easy = empty.clone();
        easy.editMeta(meta -> {
            meta.lore(lore(
                    " ",
                    "<color:#46ed40>+</color> <white>Шанс <green><u>10%</u></green> не получить урон от удушения под водой.</white>",
                    "<color:#46ed40>+</color> <white>Вся еда восстанавливает на 1 единицу сытости <green><u>больше с шансом 60%</u></green>.</white>",
                    "<color:#ebc934>●</color> <white><gold><u>Обычный урон</u></gold> от мобов.</white>",
                    "<color:#ed5a40>-</color> <white>Количество получаемого опыта на 10% <red><u>меньше</u></red>.</white>",
                    "<color:#ed5a40>-</color> <white><red><u>Невозможность</u></red> попасть в любую таблицу статистики в дискорде.</white>"
            ));
            meta.displayName(TextWrapper.text("<color:#34ebb4><u>Легкая</u></color> сложность").decoration(TextDecoration.ITALIC, false));
        });

        ItemStack hard = empty.clone();
        hard.editMeta(meta -> {
            meta.lore(lore(
                    " ",
                    "<color:#46ed40>+</color> <white><green><u>Возможность</u></green> попасть в любую таблицу статистики в дискорде.</white>",
                    "<color:#46ed40>+</color> <white>Шанс на получение более ценных ресурсов с рыбалки <green><u>выше на 7%</u></green>.</white>",
                    " ",
                    "<color:#ebc934>●</color> <white>Разная еда время от времени восстанавливает <green><u>больше</u></green> сытости,</white>",
                    "<color:#ebc934>|</color> <white>чем та, что игрок употреблял в больших количествах. Проще говоря,</white>",
                    "<color:#ebc934>|</color> <white>игроку требуется употреблять разную еду, для большей эффективности.</white>",
                    "<color:#ebc934>|</color> <white>Игрок так-же <red><u>не сможет</u></red> есть одно и тоже подряд.</white>",
                    " ",
                    "<color:#ed5a40>-</color> <white>Количество любого получаемого <red><u>урона на 15% больше</u></red>, кроме урона от удушения под водой.</white>",
                    "<color:#ed5a40>-</color> <white>При смерти, один из предметов может <red><u>исчезнуть</u></red> с шансом 0.8%.</white>",
                    "<color:#ed5a40>-</color> <white>Шанс <red><u>50%</u></red> потратить 2 или шанс <red><u>25%</u></red> потратить 3 железных самородка в столе плотника за один крафт.</white>"
            ));
            meta.displayName(TextWrapper.text("<color:#e03d6b><u>Сложная</u></color> сложность").decoration(TextDecoration.ITALIC, false));
        });

        ItemStack accept = empty.clone();
        accept.editMeta(meta -> {
            meta.lore(lore(
                    " ",
                    "<white>Надеемся, что вы сделали <gold><u>правильный</u></gold> выбор!</white>"
            ));
            meta.displayName(TextWrapper.text("<green><u>Принять</u></green> и продолжить").decoration(TextDecoration.ITALIC, false));
        });

        for (int i = 0; i < 36; i++) {
            switch (i) {
                case 3, 4, 5 -> inv.setItem(i, pick);
                case 19, 20, 21 -> inv.setItem(i, easy);
                case 23, 24, 25 -> inv.setItem(i, hard);
                case 31 -> inv.setItem(31, accept);
                default -> {}
            }
        }

        return inv;
    }

    public boolean update(InventoryView inv, int slot, Player player) {
        switch (slot) {
            case 19, 20, 21 -> {
                String line;
                switch (player.getName()) {
                    case "FelexFox", "Akcalotl", "leha_0", "Varavareva", "esco_bar_" -> line = "<color:#ed5a40>☹</color> <white>Вам недоступна легкая сложность за ваши грехи(</white>";
                    case "fnspase", "Reverted", "StreamVersus", "dizzqaz" -> line = "<color:#ed5a40>\uD83D\uDDE1</color> <white>Вы можете лучше, сделайте правильный выбор.</white>";
                    default -> {
                        inv.setTitle("§fꐮꓑ");
                        return true;
                    }
                }
                Inventory top = inv.getTopInventory();
                ItemStack easy = top.getItem(19);
                assert easy != null;

                easy.editMeta(meta -> meta.lore(lore(
                        " ",
                        line
                )));
                top.setItem(19, easy);
                top.setItem(20, easy);
                top.setItem(21, easy);
            }
            case 23, 24, 25 -> inv.setTitle("§fꐮꓐ");
            case 31 -> {
                if(inv.getTitle().contains("ꓑ") || inv.getTitle().contains("ꓐ")) {
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    return true;
                }
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private static List<Component> lore(String... lines) {
        List<Component> lore = new ArrayList<>();
        for(String line: lines) {
            lore.add(TextWrapper.text(line).decoration(TextDecoration.ITALIC, false));
        }
        return lore;
    }

}
