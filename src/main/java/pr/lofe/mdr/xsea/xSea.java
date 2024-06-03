package pr.lofe.mdr.xsea;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pr.lofe.mdr.xsea.listener.CollisionCheck;
import pr.lofe.mdr.xsea.registry.RecipesProvider;

public class xSea extends JavaPlugin {

    public static xSea I;

    private RecipesProvider recipes;

    @Override public void onEnable() {
        I = this;

        recipes = new RecipesProvider(){{
            init();
        }};

        Bukkit.getPluginManager().registerEvents(new CollisionCheck(), this);
    }

}