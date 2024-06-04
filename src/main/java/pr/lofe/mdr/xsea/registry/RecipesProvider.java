package pr.lofe.mdr.xsea.registry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.inv.CarpenterRecipe;
import pr.lofe.mdr.xsea.log.ClassLogger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipesProvider {

    private final Config data;

    private final List<CarpenterRecipe> recipes = new ArrayList<>();

    public RecipesProvider() {
        data = new Config("recipes", true);
        ClassLogger.get(RecipesProvider.class).fine("Loaded recipes configuration");
    }

    public void init() {
        recipes.clear();
        List<Material> toDisable = new ArrayList<>();
        for(String item: data.getConfig().getStringList("disabled")) {
            Material mat = Material.valueOf(item.toUpperCase());
            toDisable.add(mat);
        }

        Iterator<Recipe> recipes = Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            ItemStack result = recipes.next().getResult();
            if(toDisable.contains(result.getType())) recipes.remove();
        }
    }

    public void add(CarpenterRecipe recipe) {
        recipes.add(recipe);
    }

    public @Nullable CarpenterRecipe getRecipe(ItemStack[] matrix) {
        for(CarpenterRecipe temp: recipes) {
            ItemStack[] tempM = temp.getRecipeMatrix();

            boolean isIt = true;
            for (int i = 0; i < 5; i++) {
                if (!isSimilar(tempM[i], matrix[i])) {
                    System.out.println("there s a break at " + i);
                    isIt = false;
                    break;
                }
            }

            if(isIt) return temp;
        }
        return null;
    }

    public Config data() {
        return data;
    }

    private static boolean isSimilar(ItemStack recipe, ItemStack presented) {
        if(recipe == null) return presented == null;
        else if(presented == null) return false;

        ItemMeta recM = recipe.getItemMeta(), preM = presented.getItemMeta();
        if(!recM.hasCustomModelData()) return recipe.getType() == presented.getType();
        if(!preM.hasCustomModelData()) return false;
        return recM.getCustomModelData() == preM.getCustomModelData();
    }

}
