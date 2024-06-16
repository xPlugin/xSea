package pr.lofe.mdr.xsea.start;

import com.google.common.collect.Lists;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.entity.PlayerDifficulty;
import pr.lofe.mdr.xsea.xSea;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;

public class StartEngine implements Listener {

    public static final Config data = new Config("data", false, false);
    private final HashSet<Player> inStart = new HashSet<>();
    private final HashMap<Player, GameMode> locked = new HashMap<>();

    public StartEngine() {

    }

    public void lock(Player player) {
        locked.put(player, player.getGameMode());
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.SPECTATOR);
        Location location = new Location(Bukkit.getWorld("world"), 0, 100000, 0);
        player.teleport(location);
    }

    public void unlock(Player player) {
        player.setGameMode(locked.remove(player));
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    public void initStart(Player player) {
        inStart.add(player);
        lock(player);
        player.showTitle(Title.title(TextWrapper.text("ꐐ"), TextWrapper.text(""), Title.Times.times(Duration.ZERO, Duration.ofDays(1), Duration.ZERO)));
        player.openInventory(new ResourcePackHolder().getInventory());
    }

    @EventHandler public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(locked.containsKey(player)) event.setCancelled(true);
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
                player.sendMessage(diff.name());
                PlayerDifficulty.setDifficulty(player, diff);

                unlock(player);

                World world = Bukkit.getWorld("world");
                assert world != null;

                Location start = new Location(world, 0.5, 110.75, -1, 0, -70);
                Location end = new Location(world, .5, 113, .5, 0, 0);
                CamPath path = new CamPath(Lists.newArrayList(player.getUniqueId()), start, end, 5);
                player.teleport(end);
                path.generatePath();

                player.showTitle(Title.title(TextWrapper.text("ꐐ"), TextWrapper.text(""), Title.Times.times(Duration.ZERO, Duration.ofMillis(250), Duration.ofMillis(500))));
                path.runPath();
                inStart.remove(player);
                Bukkit.getScheduler().runTaskLater(xSea.I, () -> player.playSound(player, Sound.ENTITY_PLAYER_SWIM, 1, 1), 16L);

                data.getConfig().set(player.getName() + ".isCompletedStart", true);
                data.save();
            }
        }
    }

    @EventHandler public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if(locked.containsKey(player) && event.getInventory().getType() == InventoryType.PLAYER) {
            player.openInventory(new DifficultyHolder().getInventory());
        }
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!data.getConfig().getBoolean(player.getName() + ".isCompletedStart", false)) initStart(player);
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

            if(holder instanceof DifficultyHolder holder1) {
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
