package pr.lofe.mdr.xsea.entity.skill;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.xSea;

import java.util.ArrayList;
import java.util.List;

public class SkillRegistry implements Listener {

    @EventHandler public void onInventoryClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if(top.getHolder() instanceof SkillsHolder) {
            if(event.getClickedInventory() == top) {
                event.setCancelled(true);
                int i = event.getSlot();
                int index = i % 9;
                int row = (i - index) / 9;
                Skill skill = getSkill(row, index);
                if(skill == null) return;

                Player player = (Player) event.getWhoClicked();

                if(!doesPlayerHasSkill(player, skill.key()) && canPlayerDiscoverSkill(player, skill.key())) {
                    discoverSkill(player, skill.key());
                    player.playSound(player, "custom.misc.skill_learned", 1f, 1f);
                    SkillsHolder.modify(top, player);
                }
                else player.playSound(player, "custom.misc.skill_error", 1f, 1f);
            }
        }
    }

    @EventHandler public void onInventoryDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if(top.getHolder() instanceof SkillsHolder) event.setCancelled(true);
    }


    private static final List<ParentSkill> parentSkills = new ArrayList<>();

    public static void load() {
        ParentSkill knight = new ParentSkill(NamespacedKey.minecraft("knight"), "Убивайте и крушите!\nСтаньте сильнейшим воином.", "охотник \uD83D\uDDE1");
        knight.addSkills(
                Skill.create(false, NamespacedKey.minecraft("knight_1"), "<green>+</green> Наносите по мобам на 0.5 урона больше", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_2"), "<green>+</green> Если у игрока которого вы атакуете, здоровья\n<green>| </green> больше, чем у вас на 4 сердца, вы имеете\n<green>| </green> шанс в 30% нанести ему в 1.5 раза больше урона", null, 2),
                Skill.create(false, NamespacedKey.minecraft("knight_3"), "<green>+</green> Получайте в 1.1 раза больше опыта с убийств мобов", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_4"), "<green>+</green> Наносите по мобам на 0.5 урона больше", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_5"), "<green>+</green> Наносите по мобам на 0.5 урона больше", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_6"), "<green>+</green> Шанс 30% получить эффект \"Сила I\" на 30с<green>| </green> после убийства игрока", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_7"), "<green>+</green> Шанс 1% не получить урон если у вас меньше двух сердец.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_8"), "<green>+</green> Повысьте шанс зачаровать ваш меч на \"Остроту\" V в\n<green>| </green>столе зачарования", null, 1)
        );
        parentSkills.add(knight);

        ParentSkill fisher = new ParentSkill(NamespacedKey.minecraft("fisher"), "Добывайте рыбу обычным\nремеслом для вас, и\nвашего поселения.", "ᴘыбᴀк \uD83C\uDFA3");
        fisher.addSkills(
                Skill.create(false, NamespacedKey.minecraft("fisher_1"), "<green>+</green> Каждая убитая под водой рыба, даёт эффект\n<green>|</green> \"Грация дельфина I\" на 3 с. Длительность не суммируется.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_2"), "<green>+</green> Повысьте шанс на хорошие предметы,\n<green>|</green> при условии что один из крючков сломался.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_3"), "Невозможно получить.", null, 999),
                Skill.create(false, NamespacedKey.minecraft("fisher_4"), "Невозможно получить.", null, 999),
                Skill.create(false, NamespacedKey.minecraft("fisher_5"), "Невозможно получить.", null, 999),
                Skill.create(false, NamespacedKey.minecraft("fisher_6"), "Невозможно получить.", null, 999),
                Skill.create(false, NamespacedKey.minecraft("fisher_7"), "Невозможно получить.", null, 999),
                Skill.create(false, NamespacedKey.minecraft("fisher_8"), "Невозможно получить.", null, 999)
        );
        parentSkills.add(fisher);

        ParentSkill adventurer = new ParentSkill(NamespacedKey.minecraft("adventurer"), "Изучайте мир и его\nтайны! Вы явно станете\nлучшим рассказчиком.", "пʏтᴇшᴇствᴇнник \uD83D\uDD31");
        adventurer.addSkills(
                Skill.create(false, NamespacedKey.minecraft("adventurer_1"), "<green>+</green> Каждый сундук, открытый впервые вами, имеет\n<green>|</green> более драгоценный лут", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_2"), "<green>+</green> Получите перманентный эффект \"Скорость I\" если вы находитесь\n<green>|</green> более в чем 500 блоках от кровати или спавна.", null, 2),
                Skill.create(false, NamespacedKey.minecraft("adventurer_3"), "<green>+</green> Шанс в 50% что монстр не заметит вас, если вы находитесь на шифте.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_4"), "<green>+</green> Повысьте скорость вашего персонажа на 10%", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_5"), "<green>+</green> Повысьте скорость вашего персонажа на 15%.\n<green>|</green> Перекрывает предыдущий эффект.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_6"), "<green>+</green> Тратьте меньше голода при постоянном движении.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_7"), "<green>+</green> Тратьте ЕЩЁ меньше голода при плавнии.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_8"), "<green>+</green> Минимальное кол-во блоков для падения, что-бы получить урон,\n<green>|</green> будет равняться 6 блокам.", null, 1)
        );
        parentSkills.add(adventurer);

        ParentSkill miner = new ParentSkill(NamespacedKey.minecraft("miner"), "Добывайте драгоценности в глубинах\nэтого мира, при экстремальных условиях.", "шᴀхтᴇᴘ ⛏");
        miner.addSkills(
                Skill.create(false, NamespacedKey.minecraft("miner_1"), "<green>+</green> Получайте 1 очко прокачки за каждую вскопанную руду", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_2"), "<green>+</green> Шанс 3% получить в два раза больше лута с вскопанный руды", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_3"), "<green>+</green> Шанс 10% не потратить прочность на кирке при вскапывании руды.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_4"), "<green>+</green> Получайте 1 очко прокачки за каждую вскопанную руду", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_5"), "<green>+</green> Получайте 2 очко прокачки за каждую вскопанную руду", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_6"), "<green>+</green> Получайте в 1.5 раза меньше опыта с руд,\n<green>|</green> но добывайте в 1.2 раза больше лута\n< с одной вскопанной руды", null, 3),
                Skill.create(false, NamespacedKey.minecraft("miner_7"), "<green>+</green> Нажмите [Shift] + [F], чтобы включить режим шахтёра.\n<green>|</green> Вскапывает до 10 прилежащих руд. Тратит в два\n<green>|</green> раза больше прочности за каждый вскопанный блок.", null, 2),
                Skill.create(false, NamespacedKey.minecraft("miner_8"), "<green>+</green> Получите вечный эффект \"Спешка II\".", null, 3)
        );
        parentSkills.add(miner);

        ParentSkill protect = new ParentSkill(NamespacedKey.minecraft("protect"), "Станьте местным танком,\nи уклоняйтесь от урона.", "зᴀщитник ⛨");
        protect.addSkills(
                Skill.create(false, NamespacedKey.minecraft("protect_1"), "<green>+</green> Получайте в 1.1 раза меньше урона,\n<green>|</green> но здоровье восстанавливается медленнее", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_2"), "<green>+</green> Если вы блокируете урон щитом, с шансом 20% противник\n<green>|</green> получит четверть урона, который он нанёс", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_3"), "<green>+</green> Щит в второй руке даёт +1 к броне", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_4"), "<green>+</green> Получите на одну половинку сердца больше.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_5"), "<green>+</green> Получите на одну половинку сердца больше.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_6"), "<green>+</green> При полном комплекте незеритовой брони, получайте\n<green>|</green> вдвое меньше урона от огня.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_7"), "<green>+</green> Шанс 5% получить эффект \"Сопротивление I\" на 5с после\n<green>|</green> убийства игрока", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_8"), "<green>+</green> Получите на одну половинку сердца больше.", null, 1)
        );
        parentSkills.add(protect);

        ParentSkill builder = new ParentSkill(NamespacedKey.minecraft("builder"), "Стройте великолепные шедевры\nпод водой, и не только!", "стᴘоитᴇль \uD83E\uDE93");
        builder.addSkills(
                Skill.create(false, NamespacedKey.minecraft("builder_1"), "<green>+</green> Запас вашего кислорода перманентно на 5 секунд больше.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_2"), "<green>+</green> Минимальное кол-во блоков для падения, что-бы получить урон,\n<green>|</green> будет равняться 5 блокам.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_3"), "<green>+</green> Запас вашего кислорода перманентно на 10 секунд больше.\n<green>|</green> Перекрывает первый скилл.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_4"), "<green>+</green> Сила вашего прыжка будет на 0.1 блока выше.\n<green>|</green> Это позволит вам запрыгивать на 1.5 блока.", null, 2),
                Skill.create(false, NamespacedKey.minecraft("builder_5"), "Невозможно получить.", null, 999),
                Skill.create(false, NamespacedKey.minecraft("builder_6"), "Невозможно получить.", null, 999),
                Skill.create(false, NamespacedKey.minecraft("builder_7"), "Невозможно получить.", null, 999),
                Skill.create(false, NamespacedKey.minecraft("builder_8"), "Невозможно получить.", null, 999)
        );
        parentSkills.add(builder);
    }

    public static int getUpPoints(Player player) {
        Config data = xSea.data;
        return data.getConfig().getInt(player.getName() + ".upPoints", 0);
    }

    public static void addUpPoints(Player player, int amount) {
        Config data = xSea.data;
        int current = data.getConfig().getInt(player.getName() + ".upPoints", 0);
        current += amount;
        data.getConfig().set(player.getName() + ".upPoints", current);
        data.save();
    }

    public static boolean doesPlayerHasSkill(Player player, NamespacedKey key) {
        Config data = xSea.data;
        List<String> skills = data.getConfig().getStringList(player.getName() + ".skills");
        return skills.contains(key.toString());
    }

    public static boolean discoverSkill(Player player, NamespacedKey key) {
        if(!canPlayerDiscoverSkill(player, key)) return false;
        Skill skill = getSkill(key);
        assert skill != null;
        addUpPoints(player, -skill.currency());

        Config data = xSea.data;
        List<String> skills = data.getConfig().getStringList(player.getName() + ".skills");
        skills.add(key.toString());
        data.getConfig().set(player.getName() + ".skills", skills);
        data.save();
        return true;
    }

    public static boolean canPlayerDiscoverSkill(Player player, NamespacedKey key) {
        if(doesPlayerHasSkill(player, key)) return false;
        Skill skill = getSkill(key);
        if(skill == null) return false;
        if(getUpPoints(player) < skill.currency()) return false;

        String[] unboxed = key.toString().split("_");
        if(unboxed.length == 1) return true;

        int current = Integer.parseInt(unboxed[1]);
        if(current == 1) return doesPlayerHasSkill(player, NamespacedKey.fromString(unboxed[0]));
        else return doesPlayerHasSkill(player, NamespacedKey.minecraft(key.value().replaceAll(String.valueOf(current), String.valueOf(current - 1))));
    }

    public static Skill getSkill(NamespacedKey key) {
        for (ParentSkill parent: parentSkills) {
            if(parent.key() == key) return parent;
            Skill skill = parent.getSkill(key);
            if(skill != null) {
                return skill;
            }
        }
        return null;
    }

    public static Skill getSkill(int row, int index) {
        if(row < 0 || row > 5) return null;
        ParentSkill skill = parentSkills.get(row);
        if(index == 0) return skill;
        return skill.getSkill(index - 1);
    }

}
