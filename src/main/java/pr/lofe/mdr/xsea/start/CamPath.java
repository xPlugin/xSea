package pr.lofe.mdr.xsea.start;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
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

    private final Map<Player, Boolean> playersFlyingBefore = new HashMap<>();

    public CamPath(Location start, Location end, int durationInS) {
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

        Location step = new Location(start.getWorld(), stepX, stepY, stepZ, stepYaw, stepPitch);

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
    }

    private Player[] players;

    public void runPath(Player... players) {
        this.players = players;

        ArmorStand stand = start.getWorld().spawn(start, ArmorStand.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setInvulnerable(false);

        for (Player player : players) {
            playersFlyingBefore.put(player, player.isFlying());
        }

        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (tick == 0) {
                    stand.teleport(start);
                    Vector velocity = calculateVector(start, pathLocations.get(tick + 1));
                    stand.setVelocity(velocity);
                    for (Player player : players) player.setSpectatorTarget(stand);
                }
                else if (tick >= durationInTicks) {
                    stand.teleport(end);
                    cancel();
                    stop(stand);
                }
                else if (tick > 0 && !(tick == pathLocations.size() - 1)) {
                    stand.teleport(pathLocations.get(tick));
                    Vector velocity = calculateVector(pathLocations.get(tick), pathLocations.get(tick + 1));
                    stand.setVelocity(velocity);
                }
                tick++;
            }
        }.runTaskTimer(xSea.I, 1, 1).getTaskId();
    }


    public void stop(ArmorStand entity) {
        try {
            Bukkit.getScheduler().cancelTask(taskId);
        }catch (Exception e) {
            e.printStackTrace();
        }

        for (Player player: players) {
            player.setSpectatorTarget(null);
            player.setFlying(playersFlyingBefore.remove(player));
        }

        Bukkit.getScheduler().runTaskLater(xSea.I, entity::remove, 1L);
    }


    public Vector calculateVector(Location start, Location end) {
        return end.toVector().subtract(start.toVector());
    }


}