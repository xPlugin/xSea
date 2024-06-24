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
                Skill.create(false, NamespacedKey.minecraft("knight_1"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_2"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_3"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_4"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_5"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_6"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_7"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("knight_8"), "", null, 1)
        );
        parentSkills.add(knight);

        ParentSkill fisher = new ParentSkill(NamespacedKey.minecraft("fisher"), "Добывайте рыбу обычным\nремеслом для вас, и\nвашего поселения.", "ᴘыбᴀк \uD83C\uDFA3");
        fisher.addSkills(
                Skill.create(false, NamespacedKey.minecraft("fisher_1"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_2"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_3"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_4"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_5"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_6"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_7"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_8"), "", null, 1)
        );
        parentSkills.add(fisher);

        ParentSkill adventurer = new ParentSkill(NamespacedKey.minecraft("adventurer"), "Изучайте мир и его\nтайны! Вы явно станете\nлучшим рассказчиком.", "пʏтᴇшᴇствᴇнник \uD83D\uDD31");
        adventurer.addSkills(
                Skill.create(false, NamespacedKey.minecraft("adventurer_1"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_2"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_3"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_4"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_5"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_6"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_7"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_8"), "", null, 1)
        );
        parentSkills.add(adventurer);

        ParentSkill miner = new ParentSkill(NamespacedKey.minecraft("miner"), "Добывайте драгоценности в глубинах\nэтого мира, при экстремальных условиях.", "шᴀхтᴇᴘ ⛏");
        miner.addSkills(
                Skill.create(false, NamespacedKey.minecraft("miner_1"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_2"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_3"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_4"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_5"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_6"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_7"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_8"), "", null, 1)
        );
        parentSkills.add(miner);

        ParentSkill protect = new ParentSkill(NamespacedKey.minecraft("protect"), "Станьте местным танком,\nи уклоняйтесь от любого урона.", "зᴀщитник ⛨");
        protect.addSkills(
                Skill.create(false, NamespacedKey.minecraft("protect_1"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_2"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_3"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_4"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_5"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_6"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_7"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("protect_8"), "", null, 1)
        );
        parentSkills.add(protect);

        ParentSkill builder = new ParentSkill(NamespacedKey.minecraft("builder"), "Стройте великолепные шедевры\nпод водой, и не только!", "стᴘоитᴇль \uD83E\uDE93");
        builder.addSkills(
                Skill.create(false, NamespacedKey.minecraft("builder_1"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_2"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_3"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_4"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_5"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_6"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_7"), "", null, 1),
                Skill.create(false, NamespacedKey.minecraft("builder_8"), "", null, 1)
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
