package pr.lofe.mdr.xsea.entity.level;

import com.azlagor.LiteFish.API.events.CatchEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.google.common.collect.Lists;
import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.debug.DebugMode;
import pr.lofe.mdr.xsea.entity.DisplayUpdate;
import pr.lofe.mdr.xsea.entity.skill.SkillRegistry;
import pr.lofe.mdr.xsea.util.RandomUtil;
import pr.lofe.mdr.xsea.xSea;

import java.util.List;

public class PlayerLevel implements Listener {

    /**
     * 0 - 0
     * 1 - 100
     * 2 - 650
     * 3 - 1400
     * 4 - 2400
     * 5 - 3650
     * 6 - 5150
     * 7 - 6900
     * 8 - 7900
     * 9 - 9250
     * 10 - 10750
     * 11 - 13000
     * СПОСОБЫ ПОЛУЧЕНИЯ
     * еда (мб) *
     * рыба(средний опыт) *
     * билетики(50-500) *
     * мобы(за первого убитого 20, дальше - меньше) *
     * ачивки любые(деф - 5, цель - 15, чэлэндж влад а4 - 30) *
     * каждые 5 уровней опыта кубов +опыт, выдается один раз *
     */

    @EventHandler public void onFishCatch(CatchEvent event) {
        Player player = event.getPlayer();
        addPoints(player, RandomUtil.nextInt(10));
    }

    @EventHandler(priority = EventPriority.MONITOR) public void onExperienceEvent(PlayerPickupExperienceEvent event) {
        Player player = event.getPlayer();
        if(!event.isCancelled()) {
            Bukkit.getScheduler().runTaskLater(xSea.I, () -> {
                int level = player.getLevel();
                if(level % 5 == 0) {
                    Config data = xSea.data;
                    int maxLevel = data.getConfig().getInt(player.getName() + ".maxLevel", 0);
                    if(maxLevel < level) addPoints(player, (int) (level * 1.5));
                    data.getConfig().set(player.getName() + ".maxLevel", level);
                    data.save();
                }
            }, 1L);
        }
    }

    @EventHandler public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Advancement adv = event.getAdvancement();
        AdvancementDisplay dis = adv.getDisplay();
        if(dis != null && dis.doesShowToast()) {
            int points;
            switch (dis.frame()) {
                case TASK -> points = 5;
                case GOAL -> points = 15;
                case CHALLENGE -> points = 30;
                default -> points = 0;
            }
            addPoints(event.getPlayer(), points);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR) public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item.getItemMeta() != null) {
                PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
                if(data.has(booster_points)) {
                    Integer points = data.get(booster_points, PersistentDataType.INTEGER);
                    assert points != null;

                    if(RandomUtil.nextBool(5)) points = (int) (points * 1.5);

                    if(addPoints(player, points)) {
                        player.playEffect(EntityEffect.TOTEM_RESURRECT);
                        Bukkit.getScheduler().runTaskLater(xSea.I, () -> player.getInventory().setItemInMainHand(null), 0L);
                    }
                }
            }
        }
    }

    @EventHandler public void onEntityResurrect(EntityResurrectEvent event) {
        if(event.getEntity() instanceof Player player) {

            ItemStack item;
            if(player.getInventory().getItemInMainHand().getType() == Material.AIR) item = player.getInventory().getItemInOffHand();
            else item = player.getInventory().getItemInMainHand();

            String id = xSea.getItems().getKey(item);
            if(id != null) event.setCancelled(true);
        }
    }


    @EventHandler public void onLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();

        String sprite = "ꐑ";
        if(event.getNew() == 11) sprite = "ꓔ";
        player.showTitle(Title.title(
                TextWrapper.text(sprite),
                TextWrapper.text(String.format("%d → <green>%d</green>", event.getOld(), event.getNew()))
        ));
        player.playSound(player, "ui.toast.challenge_complete", 1f, 1f);
        DisplayUpdate.update();

        SkillRegistry.addUpPoints(player, 3);
    }

    public static boolean addPoints(Player player, int points) {
        if(points <= 0) return false;

        Config data = xSea.data;
        int oldP = data.getConfig().getInt(player.getName() + ".points", 0);

        int newP = oldP + points;
        data.getConfig().set(player.getName() + ".points", newP);
        data.save();

        String end;
        String rawNumber = String.valueOf(points);
        switch (rawNumber.charAt(rawNumber.length() - 1)) {
            case '1' -> end = "очко";
            case '2', '3', '4' -> end = "очка";
            default -> end = "очков";
        }

        player.sendActionBar(TextWrapper.text("<green>+" + points + "</green> " + end));
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f);

        int oldLevel = getLevelByPoints(oldP), newLevel = getLevelByPoints(newP);
        if(oldLevel < newLevel) {
            Bukkit.getPluginManager().callEvent(new PlayerLevelChangeEvent(player, oldLevel, newLevel));
        }
        return true;
    }

    private static int generateBoosterLevel() {
        if (RandomUtil.nextBool(40)) return 2;
        else if (RandomUtil.nextBool(20)) return 3;
        else if (RandomUtil.nextBool(15)) return 4;
        else if (RandomUtil.nextBool(5)) return 5;
        return 1;
    }

    private static final NamespacedKey booster_points = NamespacedKey.minecraft("booster_points");
    public static ItemStack generateBooster() {
        int level = generateBoosterLevel();
        int max = level == 1 ? 50 : 100;

        int points = RandomUtil.nextInt(max);
        if(level != 1) points += (level - 1) * 100;
        else points += 50;

        ItemStack item = xSea.getItems().getItem("level_booster_" + level);
        int finalPoints = points;
        item.editMeta(meta -> {
            Component name = meta.displayName();
            assert name != null;

            meta.getPersistentDataContainer().set(booster_points, PersistentDataType.INTEGER, finalPoints);

            name = name.append(TextWrapper.text(" <dark_gray>(" + finalPoints + ")</dark_gray>"));
            meta.displayName(name);
        });
        return item;
    }

    public static int getLevel(@NotNull OfflinePlayer player) {
        return getLevelByPoints(getPoints(player));
    }

    public static int getPoints(OfflinePlayer player) {
        FileConfiguration cfg = xSea.data.getConfig();
        return cfg.getInt(player.getName() + ".points", 0);
    }

    public static int getLevelByPoints(int points) {
        for (int i = 11; 0 < i; i--) {
            if(getLevelThreshold(i) <= points) return i;
        }
        return 11;
    }

    public static int getLevelThreshold(int level) {
        int threshold;
        switch (level) {
            default -> threshold = 0;
            case 2 -> threshold = 100;
            case 3 -> threshold = 650;
            case 4 -> threshold = 1400;
            case 5 -> threshold = 2400;
            case 6 -> threshold = 3650;
            case 7 -> threshold = 5150;
            case 8 -> threshold = 6900;
            case 9 -> threshold = 7900;
            case 10 -> threshold = 9250;
            case 11 -> threshold = 10750;
        }
        return threshold;
    }

}
