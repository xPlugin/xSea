package pr.lofe.mdr.xsea.registry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.log.ClassLogger;

import java.util.Iterator;

public class RecipesProvider {

    private final Config data;

    public RecipesProvider() {
        data = new Config("recipes", true);
        ClassLogger.get(RecipesProvider.class).fine("Loaded recipes configuration...");
    }

    public void init() {
        Iterator<Recipe> recipes = Bukkit.recipeIterator();
        for(String item: data.getConfig().getStringList("disabled")) {
            Material mat = Material.valueOf(item.toUpperCase());
            while (recipes.hasNext()) {
                ItemStack result = recipes.next().getResult();
                if(result.getType() == mat) recipes.remove();
            }
        }
    }

}
