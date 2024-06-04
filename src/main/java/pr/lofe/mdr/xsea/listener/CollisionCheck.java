package pr.lofe.mdr.xsea.listener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CollisionCheck implements Listener {

    @EventHandler public void onBoatBreak(VehicleBlockCollisionEvent event) {
        Vehicle vehicle = event.getVehicle();
        if(vehicle instanceof Boat boat) {
            boat.remove();
            ItemStack planks = new ItemStack(boat.getBoatType().getMaterial(), new Random().nextInt(3) + 1);
            ItemStack stick = new ItemStack(Material.STICK, new Random().nextInt(1) + 1);

            World world = boat.getWorld();
            world.dropItemNaturally(boat.getLocation(), planks);
            world.dropItemNaturally(boat.getLocation(), stick);

            boat.getWorld().playSound(boat.getLocation().add(0, .3, 0), "entity.zombie.attack_wooden_door", 1f, 1.25f);
        }
    }

     @EventHandler public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(player.getVehicle() instanceof Boat boat) {
            double boatWidth = 1.5;
            double halfWidth = boatWidth / 2;

            double boatX = boat.getLocation().getX();
            double boatY = boat.getLocation().getY();
            double boatZ = boat.getLocation().getZ();

            double[][] corners = {
                    {boatX - halfWidth, boatZ - halfWidth},
                    {boatX - halfWidth, boatZ + halfWidth},
                    {boatX + halfWidth, boatZ - halfWidth},
                    {boatX + halfWidth, boatZ + halfWidth}
            };

            for (double[] corner : corners) {
                Block block = boat.getWorld().getBlockAt((int) Math.floor(corner[0]), (int) Math.floor(boatY), (int) Math.floor(corner[1]));
                if (!block.isPassable()) {
                    VehicleBlockCollisionEvent collisionEvent = new VehicleBlockCollisionEvent(boat, block);
                    Bukkit.getServer().getPluginManager().callEvent(collisionEvent);
                    return;
                }
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

}
