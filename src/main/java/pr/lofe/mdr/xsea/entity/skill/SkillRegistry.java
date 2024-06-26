package pr.lofe.mdr.xsea.entity.skill;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.debug.DebugMode;
import pr.lofe.mdr.xsea.entity.level.PlayerLevel;
import pr.lofe.mdr.xsea.util.LocationUtil;
import pr.lofe.mdr.xsea.util.RandomUtil;
import pr.lofe.mdr.xsea.xSea;

import java.util.*;

public class SkillRegistry implements Listener {

    private final CoreProtectAPI coreProtectAPI = CoreProtect.getInstance().getAPI();

    public SkillRegistry() {
        tasks();
    }

    public void tasks() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(xSea.I, () -> {
            for(Player player: Bukkit.getOnlinePlayers()) {
                if(doesPlayerHasSkill(player, NamespacedKey.minecraft("adventurer_2"))) {
                    Location respawn = player.getRespawnLocation();
                    if(respawn == null) respawn = new Location(player.getWorld(), 0, 113, 0);
                    if(LocationUtil.distance(respawn, player.getLocation()) >= 500) {
                        PotionEffect effect = player.getPotionEffect(PotionEffectType.SPEED);
                        if(effect == null || !effect.isInfinite()) player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, true, true, true));
                    }
                    else {
                        PotionEffect effect = player.getPotionEffect(PotionEffectType.SPEED);
                        if(effect != null && effect.isInfinite()) player.removePotionEffect(PotionEffectType.SPEED);
                    }
                }

                if(doesPlayerHasSkill(player, NamespacedKey.minecraft("miner_7"))) {
                    PotionEffect effect = player.getPotionEffect(PotionEffectType.HASTE);
                    if(effect == null) player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, -1, 1, true, false, true));
                }
            }
        }, 0L, 20L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(xSea.I, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemStack item = player.getInventory().getItemInOffHand();

                if(item.getType() == Material.SHIELD) {
                    ItemMeta meta = item.getItemMeta();
                    Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.GENERIC_ARMOR);
                    if(doesPlayerHasSkill(player, NamespacedKey.minecraft("protect_3"))) {
                        if(modifiers == null || modifiers.isEmpty()) {
                            meta.addAttributeModifier(
                                    Attribute.GENERIC_ARMOR,
                                    new AttributeModifier(UUID.randomUUID(), "generic.armor", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND)
                            );
                            item.setItemMeta(meta);
                        }
                    }
                    else {
                        if(modifiers != null) meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
                    }
                }
            }
        }, 0L, 40L);
    }

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

    @EventHandler public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player player) {
            if(event.getEntity().getType() != EntityType.PLAYER) {
                double damage = event.getDamage();

                if(doesPlayerHasSkill(player, NamespacedKey.minecraft("knight_5"))) damage += .5;
                if(doesPlayerHasSkill(player, NamespacedKey.minecraft("knight_4"))) damage += .5;
                if(doesPlayerHasSkill(player, NamespacedKey.minecraft("knight_1"))) damage += .5;

                event.setDamage(damage);
            }
            else {
                Player damaged = (Player) event.getEntity();
                if(damaged.getHealth() + 8 > player.getHealth()) {
                    if(RandomUtil.nextBool(30)) event.setDamage(event.getDamage() * 1.5);
                }

                if(doesPlayerHasSkill(player, NamespacedKey.minecraft("protect_2"))) {
                    if(player.isBlocking() && RandomUtil.nextBool(20)) damaged.damage(event.getDamage() * 0.25, DamageSource.builder(DamageType.THORNS).build());
                }
            }
        }
    }

    @EventHandler public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getPlayer().getKiller();
        if(killer != null) {
            if(doesPlayerHasSkill(killer, NamespacedKey.minecraft("knight_6"))) {
                if(RandomUtil.nextBool(30)) killer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 0, true, true, true), false);
            }

            if(doesPlayerHasSkill(killer, NamespacedKey.minecraft("protect_7"))) {
                if(RandomUtil.nextBool(5)) killer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 0, true, true, true));
            }
        }
    }

    @EventHandler public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(doesPlayerHasSkill(player, NamespacedKey.minecraft("protect_1"))) event.setDamage(event.getDamage() * 0.9);

            if(doesPlayerHasSkill(player, NamespacedKey.minecraft("knight_7")))
                if(player.getHealth() < 4 && RandomUtil.nextBool(1)) event.setCancelled(true);

            if(doesPlayerHasSkill(player, NamespacedKey.minecraft("protect_6")) && event.getDamageSource().getDamageType().key().value().contains("_FIRE")) {
                for (ItemStack item : player.getInventory().getArmorContents()) {
                    if(item == null || !item.getType().name().contains("NETHERITE")) return;
                }

                event.setDamage(event.getDamage() * 0.5);
            }
        }
    }

    @EventHandler public void onEntityDeath(EntityDeathEvent event) {
        switch (event.getEntityType()) {
            case COD, TROPICAL_FISH, SALMON, PUFFERFISH -> {
                Player killer = event.getEntity().getKiller();
                if(killer != null && doesPlayerHasSkill(killer, NamespacedKey.minecraft("fisher_1"))) {
                    killer.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 100, 0, true, true, true), false);
                }
            }
            default -> {}
        }
    }

    @EventHandler public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if(event.getTarget() instanceof Player player) {
            if(doesPlayerHasSkill(player, NamespacedKey.minecraft("adventurer_3"))) {
                if(player.isSneaking() && RandomUtil.nextBool(50)) event.setCancelled(true);
            }
        }
    }

    private final static double DEFAULT_SPEED = 0.10000000149011612;
    private final static double DEFAULT_JUMP_STRENGTH = 0.41888888688697815;

    @EventHandler public void onSkillDiscover(SkillDiscoverEvent event) {
        NamespacedKey key = event.getSkill().key();
        Player player = event.getPlayer();

        String str = key.toString();
        if(str.contains("adventurer_5")) player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(DEFAULT_SPEED + 0.015);
        else if(str.contains("adventurer_4")) player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(DEFAULT_SPEED + 0.01);

        if(str.contains("adventurer_8")) player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(5);
        else if (str.contains("builder_2")) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE);
            if(attribute != null) {
                if(attribute.getBaseValue() < 4) attribute.setBaseValue(4);
            }
        }

        switch (key.value()) {
            case "adventurer_8" -> player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(5);
            case "builder_4" -> player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(DEFAULT_JUMP_STRENGTH + 0.1);
            case "protect_4", "protect_5", "protect_8" -> {
                AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if(attribute != null) {
                    attribute.setBaseValue(attribute.getBaseValue() + 1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR) public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!event.isCancelled()) {
            Player player = (Player) event.getEntity();
            if(player.getFoodLevel() > event.getFoodLevel()) {
                if(doesPlayerHasSkill(player, NamespacedKey.minecraft("adventurer_7")) && player.isInWater()) {
                    if(RandomUtil.nextBool(40)) event.setCancelled(true);
                }
                else if(doesPlayerHasSkill(player, NamespacedKey.minecraft("adventurer_7"))) if(RandomUtil.nextBool(20)) event.setCancelled(true);
            }
        }
    }

    @EventHandler public void onBlockBreak(BlockBreakEvent event) {
        if(!event.isCancelled()) {
            Block block = event.getBlock();
            Player player = event.getPlayer();

            if (block.getType().name().contains("_ORE")) {

                if(RandomUtil.nextBool(3)) {
                    List<ItemStack> items = new ArrayList<>(event.getBlock().getDrops(player.getActiveItem(), player));
                    Location loc = block.getLocation().add(.5, .2, .5);
                    for (ItemStack item : items) {
                        block.getWorld().dropItemNaturally(loc, item);
                    }
                }


                int summary = 0;

                if (doesPlayerHasSkill(player, NamespacedKey.minecraft("miner_1"))) summary++;
                if (doesPlayerHasSkill(player, NamespacedKey.minecraft("miner_4"))) summary++;
                if (doesPlayerHasSkill(player, NamespacedKey.minecraft("miner_5"))) summary++;

                if (summary > 0) {
                    List<String[]> results = coreProtectAPI.blockLookup(block, 1814400000);
                    for (String[] strings : results) {
                        if (coreProtectAPI.parseResult(strings).getActionString().equals("place")) return;
                    }
                }

                PlayerLevel.addPoints(player, summary);
            }
        }
    }

    @EventHandler public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if(event.getItem().getType().name().contains("PICKAXE")) {
            Player player = event.getPlayer();
            if(doesPlayerHasSkill(player, NamespacedKey.minecraft("miner_3")) && RandomUtil.nextBool(10)) event.setCancelled(true);
        }
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
                Skill.create(false, NamespacedKey.minecraft("knight_8"), "<green>+</green> Повысьте шанс зачаровать ваш меч на \"Остроту\" V в\n<green>| </green>столе зачарования", null, 1) // scam
        );
        parentSkills.add(knight);

        ParentSkill fisher = new ParentSkill(NamespacedKey.minecraft("fisher"), "Добывайте рыбу обычным\nремеслом для вас, и\nвашего поселения.", "ᴘыбᴀк \uD83C\uDFA3");
        fisher.addSkills(
                Skill.create(false, NamespacedKey.minecraft("fisher_1"), "<green>+</green> Каждая убитая под водой рыба, даёт эффект\n<green>|</green> \"Грация дельфина I\" на 5 с. Длительность не суммируется.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("fisher_2"), "<green>+</green> Повысьте шанс на хорошие предметы,\n<green>|</green> при условии что один из крючков сломался.", null, 1), // scam
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
                Skill.create(false, NamespacedKey.minecraft("adventurer_1"), "<green>+</green> Каждый сундук, открытый впервые вами, имеет\n<green>|</green> более драгоценный лут", null, 1), // scam
                Skill.create(false, NamespacedKey.minecraft("adventurer_2"), "<green>+</green> Получите перманентный эффект \"Скорость I\" если вы находитесь\n<green>|</green> более в чем 500 блоках от кровати или спавна.", null, 2),
                Skill.create(false, NamespacedKey.minecraft("adventurer_3"), "<green>+</green> Шанс в 50% что монстр не заметит вас, если вы находитесь на шифте.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_4"), "<green>+</green> Повысьте скорость вашего персонажа на 10%", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_5"), "<green>+</green> Повысьте скорость вашего персонажа на 15%.\n<green>|</green> Перекрывает предыдущий эффект.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_6"), "<green>+</green> Тратьте меньше голода.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_7"), "<green>+</green> Тратьте ЕЩЁ меньше голода при плавнии.", null, 1),
                Skill.create(false, NamespacedKey.minecraft("adventurer_8"), "<green>+</green> Минимальное кол-во блоков для падения, что-бы получить урон,\n<green>|</green> будет равняться 6 блокам.", null, 1)
        );
        parentSkills.add(adventurer);

        ParentSkill miner = new ParentSkill(NamespacedKey.minecraft("miner"), "Добывайте драгоценности в глубинах\nэтого мира, при экстремальных условиях.", "шᴀхтᴇᴘ ⛏");
        miner.addSkills(
                Skill.create(false, NamespacedKey.minecraft("miner_1"), "<green>+</green> Получайте 1 очко прокачки за каждую вскопанную руду", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_2"), "<green>+</green> Шанс 3% получить в два раза больше лута с вскопанный руды", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_3"), "<green>+</green> Шанс 10% не потратить прочность на кирке", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_4"), "<green>+</green> Получайте 1 очко прокачки за каждую вскопанную руду", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_5"), "<green>+</green> Получайте 1 очко прокачки за каждую вскопанную руду", null, 1),
                Skill.create(false, NamespacedKey.minecraft("miner_6"), "<green>+</green> Получайте в 1.5 раза меньше опыта с руд,\n<green>|</green> но добывайте в 1.2 раза больше лута\n<green>|</green> с одной вскопанной руды", null, 1), // TODO
                Skill.create(false, NamespacedKey.minecraft("miner_7"), "<green>+</green> Получите вечный эффект \"Спешка II\".", null, 2),
                Skill.create(false, NamespacedKey.minecraft("miner_8"), "Невозможно получить.", null, 999)
        );
        parentSkills.add(miner);

        ParentSkill protect = new ParentSkill(NamespacedKey.minecraft("protect"), "Станьте местным танком,\nи уклоняйтесь от урона.", "зᴀщитник ⛨");
        protect.addSkills(
                Skill.create(false, NamespacedKey.minecraft("protect_1"), "<green>+</green> Получайте 90% от урона,\n<green>|</green> но здоровье восстанавливается медленнее", null, 1),
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

        Bukkit.getPluginManager().callEvent(new SkillDiscoverEvent(player, skill));

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
