package pr.lofe.mdr.xsea.listener;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;

public class BoatListener implements Listener {

    @EventHandler public void onBoatBreak(VehicleBlockCollisionEvent event) {
        Vehicle vehicle = event.getVehicle();
        if(vehicle instanceof Boat boat) {
            Entity ent = boat.getPassengers().get(0);
            if(ent instanceof Player player) {
                player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1f, 2f));
                player.sendActionBar(Component.text("Hit! " + event.getBlock().getType()));
            }
        }
    }

}
