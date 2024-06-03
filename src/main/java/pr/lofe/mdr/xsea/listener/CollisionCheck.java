package pr.lofe.mdr.xsea.listener;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CollisionCheck implements Listener {

    @EventHandler public void onBoatBreak(VehicleBlockCollisionEvent event) {
        Vehicle vehicle = event.getVehicle();
        if(vehicle instanceof Boat) {
            Player player = Bukkit.getPlayer("just_l0fe");
            player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1f, 2f));
            player.sendActionBar(Component.text("Hit! " + event.getBlock().getType()));
        }
    }

    /* @EventHandler public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(player.getVehicle() instanceof Boat boat) {
            Block block = checkCollision(boat.getLocation(), boat, terpi);
            if(block != null) {
                Bukkit.getPluginManager().callEvent(
                        new VehicleBlockCollisionEvent(boat, block, boat.getVelocity())
                );
            }
        }
    } */

    @EventHandler public void onVehicleMove(VehicleMoveEvent event) {
        if(event.getVehicle() instanceof Boat boat) {
            Vector vector = event.getTo().toVector().subtract(event.getFrom().toVector()).normalize();

            Block block = checkCollision(boat.getLocation(), boat, vector);
            if(block != null) {
                Bukkit.getPluginManager().callEvent(
                        new VehicleBlockCollisionEvent(boat, block, event.getVehicle().getVelocity())
                );
            }
        }
    }

    @EventHandler public void onVehicleDestroy(VehicleDestroyEvent event) {
        if(event.getVehicle() instanceof Boat boat) {
            event.setCancelled(true);
            boat.remove();
            ItemStack planks = new ItemStack(boat.getBoatType().getMaterial(), new Random().nextInt(3) + 1);
            ItemStack stick = new ItemStack(Material.STICK, new Random().nextInt(1) + 1);

            World world = boat.getWorld();
            world.dropItemNaturally(boat.getLocation(), planks);
            world.dropItemNaturally(boat.getLocation(), stick);
        }
    }

    @SuppressWarnings("deprecation")
    private Block checkCollision(Location bloc, Boat boat, Vector vector){
        RayTraceResult result = bloc.getBlock().rayTrace(bloc, vector, 3, FluidCollisionMode.NEVER);
        if(result == null) return null;

        Block b = result.getHitBlock();
        if(b == null) return null;
        if(!b.getType().isSolid()) return null;

        Entity entity = boat.getPassenger();
        if(entity instanceof Player player && isPlayerOnHalf(player)) {
            if(bloc.getBlockY() == NumberConversions.ceil(bloc.getY())) return b;
            return null;
        }
        return b;
    }

    private static final List<Material> halfBlockTypes = Arrays.stream(Material.values()).filter(m->m.name().endsWith("_SLAB")||m.name().endsWith("_STAIRS")).collect(Collectors.toList());

    public boolean isPlayerOnHalf(Player player) {
        return (!player.isFlying() && (new DecimalFormat("#.#").format(player.getLocation().y())).endsWith("5")) || halfBlockTypes.contains(player.getLocation().getBlock().getType());
    }

}
