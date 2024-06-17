package pr.lofe.mdr.xsea.start;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pr.lofe.mdr.xsea.xSea;

import java.util.*;

public class CamPath {

    private final Location start;
    private final Location end;

    private final int durationInTicks;
    private int tick = 0;
    private int taskId;

    private final List<Location> pathLocations = new ArrayList<>();
    private final List<Player> players;

    private final Map<Player, GameMode> playersGamemodeBefore = new HashMap<>();
    private final Map<Player, Location> playersLocationBefore = new HashMap<>();
    private final Map<Player, Boolean> playersFlyingBefore = new HashMap<>();

    public CamPath(List<Player> players, Location start, Location end, int durationInS) {
        this.players = players;
        this.start = start;
        this.end = end;
        this.durationInTicks = durationInS * 20;
    }

    public void generatePath() {
        double stepX = (end.getX() - start.getX()) / durationInTicks;
        double stepY = (end.getY() - start.getY()) / durationInTicks;
        double stepZ = (end.getZ() - start.getZ()) / durationInTicks;
        float stepYaw = (end.getYaw() - start.getYaw()) / durationInTicks;
        float stepPitch = (end.getPitch() - start.getPitch()) / durationInTicks;

        Location step = new Location(Bukkit.getWorld("world"), stepX, stepY, stepZ, stepYaw, stepPitch);

        pathLocations.add(start);
        for (int i = 1; i <= durationInTicks; i++) {
            Location prevLocation = pathLocations.get(i-1).clone();
            Location nextlocation = prevLocation.add(step);

            float yaw = nextlocation.getYaw() + stepYaw;
            float pitch = nextlocation.getPitch() + stepPitch;
            nextlocation.setYaw(yaw);
            nextlocation.setPitch(pitch);

            pathLocations.add(nextlocation);
        }

        for (Player player: players) {
            playersGamemodeBefore.put(player, player.getGameMode());
            playersLocationBefore.put(player, player.getLocation());
            playersFlyingBefore.put(player, player.isFlying());
        }

        runPath();
    }


    public void runPath() {
        taskId =  new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : players) {
                    if (tick == 0) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(start);
                        Vector velocity = calculateVector(start, pathLocations.get(tick + 1));
                        player.setVelocity(velocity);
                    }
                    else if (tick >= durationInTicks) {
                        player.teleport(end);
                        cancel();
                        stop();
                    }
                    else if (tick > 0 && !(tick == pathLocations.size() - 1)) {
                        player.teleport(pathLocations.get(tick));
                        Vector velocity = calculateVector(pathLocations.get(tick), pathLocations.get(tick + 1));
                        player.setVelocity(velocity);
                    }

                }
                tick++;
            }
        }.runTaskTimer(xSea.I, 1, 1).getTaskId();
    }


    public void stop() {
        try {
            Bukkit.getScheduler().cancelTask(taskId);
        }catch (Exception e) {
            e.printStackTrace();
        }

        for (Player player: players) {
            player.setGameMode(playersGamemodeBefore.remove(player));
            player.teleport(playersLocationBefore.remove(player));
            player.setFlying(playersFlyingBefore.remove(player));
        }

    }


    public Vector calculateVector(Location start, Location end) {
        return end.toVector().subtract(start.toVector());
    }


}