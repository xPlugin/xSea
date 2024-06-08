package pr.lofe.mdr.xsea;

import dev.jorel.commandapi.CommandAPI;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.command.SeaCommand;
import pr.lofe.mdr.xsea.enchant.EnchantmentHandler;
import pr.lofe.mdr.xsea.enchant.WaterResistance;
import pr.lofe.mdr.xsea.inv.InventoryListener;
import pr.lofe.mdr.xsea.item.ItemRegistry;
import pr.lofe.mdr.xsea.listener.*;
import pr.lofe.mdr.xsea.loader.AnonymousLoader;
import pr.lofe.mdr.xsea.registry.RecipesProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

public class xSea extends JavaPlugin {

    public static xSea I;

    private RecipesProvider recipes;
    private ItemRegistry items;

    public static WaterResistance WATER_RESISTANCE;

    @Override public void onEnable() {
        Bukkit.broadcast(TextWrapper.text("<blue>[xSea]</blue> [patched]<br>Debug mode <u><green>enabled</green></u>, saving StackTrace`s to log file."));

        I = this;
        reloadData();

        AnonymousLoader.a();

        SimpleDateFormat now = new SimpleDateFormat("dd.MM HH:mm:ss");

        ItemListener itemListener = new ItemListener();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            String date = now.format(new Date());
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendActionBar(TextWrapper.text(player.getName() + " | " + date));
            });
            itemListener.itemStep();
        }, 200L, 20L);

        new SeaCommand().register();
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(itemListener, this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EnchantmentHandler(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
    }

    public void reloadData() {
        items = new ItemRegistry();
        items.init();

        recipes = new RecipesProvider();
        recipes.init();
    }

    @Override
    public void onDisable() {
        CommandAPI.unregister("sea");
        Bukkit.removeRecipe(NamespacedKey.minecraft("carpenter_table"));
        Bukkit.removeRecipe(NamespacedKey.minecraft("flippers_recipe"));
    }

    public static RecipesProvider getRecipes() {
        return I.recipes;
    }
    public static ItemRegistry getItems() { return I.items; }
}