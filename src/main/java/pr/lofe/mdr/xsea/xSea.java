package pr.lofe.mdr.xsea;

import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.command.SeaCommand;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.enchant.EnchantmentHandler;
import pr.lofe.mdr.xsea.enchant.WaterResistance;
import pr.lofe.mdr.xsea.entity.DisplayUpdate;
import pr.lofe.mdr.xsea.entity.TasksRegistry;
import pr.lofe.mdr.xsea.entity.level.PlayerLevel;
import pr.lofe.mdr.xsea.inv.InventoryListener;
import pr.lofe.mdr.xsea.registry.ItemRegistry;
import pr.lofe.mdr.xsea.listener.*;
import pr.lofe.mdr.xsea.loader.AnonymousLoader;
import pr.lofe.mdr.xsea.registry.RecipesProvider;
import pr.lofe.mdr.xsea.start.StartEngine;

public class xSea extends JavaPlugin {

    public static Config data;

    public static xSea I;

    private TasksRegistry tasks;
    private RecipesProvider recipes;
    private ItemRegistry items;

    public static WaterResistance WATER_RESISTANCE;

    @Override public void onEnable() {
        I = this;
        reloadData();

        data = new Config("data", false, false);
        tasks = new TasksRegistry();

        try { AnonymousLoader.load(); }
        catch (Exception e) { e.printStackTrace(); }

        ItemListener itemListener = new ItemListener();
        EntityListener entityListener = new EntityListener();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            itemListener.itemStep();
            entityListener.damage();
        }, 200L, 20L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, DisplayUpdate::update,200L, 200L);

        new SeaCommand().register();

        register(new InventoryListener());
        register(itemListener);
        register(new BlockListener());
        register(new EnchantmentHandler());
        register(entityListener);
        register(new StartEngine());
        register(new PlayerLevel());

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
        Bukkit.removeRecipe(NamespacedKey.minecraft("oxygen_tank_light_recipe"));
        Bukkit.removeRecipe(NamespacedKey.minecraft("oxygen_tank_recipe"));
        Bukkit.removeRecipe(NamespacedKey.minecraft("titanium_ingot_recipe"));


        DisplayUpdate.remove();
        // ChangesDetect.onDisable();
    }

    public static RecipesProvider getRecipes() {
        return I.recipes;
    }
    public static ItemRegistry getItems() { return I.items; }
    public static TasksRegistry getTasks() { return I.tasks; }
}