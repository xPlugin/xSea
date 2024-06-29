package pr.lofe.mdr.xsea.start;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.entity.DisplayUpdate;
import pr.lofe.mdr.xsea.entity.PlayerDifficulty;
import pr.lofe.mdr.xsea.xSea;

import java.time.Duration;
import java.util.HashSet;

public class StartEngine implements Listener {

    private final HashSet<Player> inStart = new HashSet<>();
    private final HashSet<Player> locked = new HashSet<>();

    public StartEngine() {

    }

    public void lock(Player player) {
        locked.add(player);
        player.setAllowFlight(true);
        player.setFlying(true);

        Location location = new Location(Bukkit.getWorld("world"), 0, 100000, 0);
        player.teleport(location);
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void unlock(Player player) {
        locked.remove(player);
    }

    public void initStart(Player player) {
        inStart.add(player);
        lock(player);
        player.showTitle(Title.title(TextWrapper.text("ꐐ"), TextWrapper.text(""), Title.Times.times(Duration.ZERO, Duration.ofDays(1), Duration.ZERO)));
        player.openInventory(new ResourcePackHolder().getInventory());
    }



    public static void animPart(Player player, String string) {
        switch (string) {
            case "preview" -> {
                ItemStack pumpkin = new ItemStack(Material.CARVED_PUMPKIN){{
                    addEnchantment(Enchantment.BINDING_CURSE, 1);
                    addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    editMeta(meta -> {
                        meta.setEnchantmentGlintOverride(false);
                        meta.displayName(TextWrapper.text(""));
                    });
                }};

                player.getInventory().setHelmet(pumpkin);
                player.showTitle(Title.title(TextWrapper.text("ꐐ"), TextWrapper.text(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(500), Duration.ofMillis(500))));
                animPart(player, "pop_up");
            }

            case "pop_up" -> {
                World overworld = Bukkit.getWorld("world");
                assert overworld != null;

                Location start = new Location(overworld, 4, 110.75, 0.5, 90, -70);
                Location end = new Location(overworld, 2.5, 113.2, .5, 90, 0);
                CamPath path = new CamPath(start, end, 3);

                path.generatePath();
                path.runPath(player);

                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, true, false, false));
                player.showTitle(Title.title(TextWrapper.text("ꐐ"), TextWrapper.text(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(250), Duration.ofMillis(500))));
                wait(() -> player.playSound(player, Sound.ENTITY_PLAYER_SWIM, 1, 1), 16L);
                wait(() -> {
                    player.showTitle(Title.title(TextWrapper.text("ꐐ"), TextWrapper.text(""), Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(500), Duration.ofMillis(150))));
                    wait(() -> {
                        player.getInventory().setHelmet(null);
                        player.removePotionEffect(PotionEffectType.SLOWNESS);
                        player.setGameMode(GameMode.SURVIVAL);
                    }, 10L);
                }, 60L);

                xSea.data.getConfig().set(player.getName() + ".isCompletedStart", true);
                xSea.data.save();

                DisplayUpdate.update();
            }
            default -> {}
        }
    }


    private static void wait(Runnable runnable, long ticks) {
        Bukkit.getScheduler().runTaskLater(xSea.I, runnable, ticks);
    }

    @EventHandler public void onPlayerStopSpectating(PlayerStopSpectatingEntityEvent event) {
        if(event.getSpectatorTarget().getPersistentDataContainer().has(CamPath.TAG)) event.setCancelled(true);
    }

    @EventHandler public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(locked.contains(player)) event.setCancelled(true);
    }

    @EventHandler public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if(inStart.contains(player) && inv.getHolder() instanceof StartHolder) {
            if (event.getReason() == InventoryCloseEvent.Reason.PLAYER) player.openInventory(event.getInventory());
            else if (inv.getHolder() instanceof DifficultyHolder && event.getReason() == InventoryCloseEvent.Reason.PLUGIN) {

                @SuppressWarnings("deprecation")
                String title = event.getView().getTitle();
                PlayerDifficulty diff = null;
                if(title.contains("ꓑ")) diff = PlayerDifficulty.EASY;
                else if (title.contains("ꓐ")) diff = PlayerDifficulty.HARD;
                assert diff != null;
                PlayerDifficulty.setDifficulty(player, diff);
                unlock(player);

                animPart(player, "preview");
                inStart.remove(player);
            }
        }
    }

    @EventHandler public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if(locked.contains(player) && event.getInventory().getType() == InventoryType.PLAYER) {
            player.openInventory(new DifficultyHolder().getInventory());
        }
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!xSea.data.getConfig().getBoolean(player.getName() + ".isCompletedStart", false)) initStart(player);
    }

    @EventHandler public void onInventoryDrag(InventoryDragEvent event) {
        if(event.getInventory().getHolder() instanceof StartHolder) event.setCancelled(true);
    }

    @EventHandler public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if(inv != null) {
            InventoryHolder holder = inv.getHolder();
            if(!(holder instanceof StartHolder)) return;
            event.setCancelled(true);

            int slot = event.getSlot();
            Player player = (Player) event.getWhoClicked();

            if(holder instanceof DifficultyHolder holder1 && inStart.contains(player)) {
                boolean doesSomethingUpdated = holder1.update(event.getView(), slot, player);
                if(doesSomethingUpdated) {
                    player.playSound(player, "custom.sfx.menu-click", 1f, 1f);
                }
            }
            else if (holder instanceof ResourcePackHolder) {
                if(slot >= 19 && slot <= 25) {
                    player.openInventory(new DifficultyHolder().getInventory());
                    player.playSound(player, "custom.sfx.menu-click", 1f, 1f);
                }
            }
        }
    }

}
