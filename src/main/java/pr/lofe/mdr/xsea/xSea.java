package pr.lofe.mdr.xsea;

import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.command.SeaCommand;
import pr.lofe.mdr.xsea.enchant.EnchantmentHandler;
import pr.lofe.mdr.xsea.enchant.WaterResistance;
import pr.lofe.mdr.xsea.inv.InventoryListener;
import pr.lofe.mdr.xsea.registry.ItemRegistry;
import pr.lofe.mdr.xsea.listener.*;
import pr.lofe.mdr.xsea.loader.AnonymousLoader;
import pr.lofe.mdr.xsea.registry.RecipesProvider;
import pr.lofe.mdr.xsea.start.StartEngine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class xSea extends JavaPlugin {

    public static xSea I;

    private RecipesProvider recipes;
    private ItemRegistry items;

    public static WaterResistance WATER_RESISTANCE;

    @Override public void onEnable() {
        I = this;
        reloadData();

        try { AnonymousLoader.load(); }
        catch (Exception e) { e.printStackTrace(); }

        SimpleDateFormat now = new SimpleDateFormat("dd.MM HH:mm:ss");

        ItemListener itemListener = new ItemListener();
        EntityListener entityListener = new EntityListener();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            String date = now.format(new Date());
            Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(TextWrapper.text(player.getName() + " | " + date)));
            itemListener.itemStep();
            entityListener.damage();
        }, 200L, 20L);

        new SeaCommand().register();

        register(new InventoryListener());
        register(itemListener);
        register(new BlockListener());
        register(new EnchantmentHandler());
        register(entityListener);
        register(new StartEngine());

        Bukkit.broadcast(TextWrapper.text("<blue>[xSea]</blue> [enabled]<br>Debug mode <u><green>enabled</green></u>, saving StackTrace`s to log file."));
    }

    private void register(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
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

        // ChangesDetect.onDisable();
    }

    public static RecipesProvider getRecipes() {
        return I.recipes;
    }
    public static ItemRegistry getItems() { return I.items; }
}