package pr.lofe.mdr.xsea;

import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pr.lofe.mdr.xsea.command.DebugCommand;
import pr.lofe.mdr.xsea.command.SeaCommand;
import pr.lofe.mdr.xsea.command.SkillsCommand;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.debug.DebugMode;
import pr.lofe.mdr.xsea.enchant.EnchantmentHandler;
import pr.lofe.mdr.xsea.enchant.WaterResistance;
import pr.lofe.mdr.xsea.entity.DisplayUpdate;
import pr.lofe.mdr.xsea.entity.level.PlayerLevel;
import pr.lofe.mdr.xsea.entity.skill.SkillRegistry;
import pr.lofe.mdr.xsea.inv.InventoryListener;
import pr.lofe.mdr.xsea.registry.ItemRegistry;
import pr.lofe.mdr.xsea.listener.*;
import pr.lofe.mdr.xsea.loader.AnonymousLoader;
import pr.lofe.mdr.xsea.registry.RecipesProvider;
import pr.lofe.mdr.xsea.start.StartEngine;

public class xSea extends JavaPlugin {

    public static Config data;

    public static xSea I;

    private RecipesProvider recipes;
    private ItemRegistry items;

    public static WaterResistance WATER_RESISTANCE;

    @Override public void onEnable() {
        I = this;
        reloadData();

        data = new Config("data", false, false);

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
        new SkillsCommand().register();
        new DebugCommand().register();

        registerListener(new InventoryListener());
        registerListener(itemListener);
        registerListener(new BlockListener());
        registerListener(new EnchantmentHandler());
        registerListener(entityListener);
        registerListener(new StartEngine());
        registerListener(new PlayerLevel());
        registerListener(new SkillRegistry());

        DebugMode.global("<blue>[xSea]</blue> <green>[reloaded]</green>");
    }

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void reloadData() {
        items = new ItemRegistry();
        items.init();

        recipes = new RecipesProvider();
        recipes.init();

        SkillRegistry.load();
    }

    @Override
    public void onDisable() {
        CommandAPI.unregister("sea");
        CommandAPI.unregister("plugins");
        CommandAPI.unregister("skills");
        CommandAPI.unregister("отладка");
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
}