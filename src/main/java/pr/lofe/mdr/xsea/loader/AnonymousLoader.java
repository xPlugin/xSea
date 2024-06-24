package pr.lofe.mdr.xsea.loader;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import pr.lofe.mdr.xsea.enchant.WaterResistance;
import pr.lofe.mdr.xsea.inv.CarpenterRecipe;
import pr.lofe.mdr.xsea.registry.ItemRegistry;
import pr.lofe.mdr.xsea.registry.RecipesProvider;
import pr.lofe.mdr.xsea.xSea;

import java.util.HashMap;

import static pr.lofe.mdr.xsea.xSea.WATER_RESISTANCE;

public class AnonymousLoader {

    public static void load() {
        ItemRegistry items = xSea.getItems();
        RecipesProvider recipes = xSea.getRecipes();

        WATER_RESISTANCE = new WaterResistance();
        Bukkit.getPluginManager().registerEvents(WATER_RESISTANCE, xSea.I);

        ItemStack oak_plank = items.getItem("oak_plank");
        oak_plank.setAmount(8);
        new CarpenterRecipe(NamespacedKey.minecraft("plank"), oak_plank){{
            setItems("PAAAP", new HashMap<>(){{
                put('P', new ItemStack(Material.OAK_PLANKS));
                put('A', null);
            }});
            recipes.add(this);
        }};

        new CarpenterRecipe(NamespacedKey.minecraft("boat_bottom"), items.getItem("boat_bottom")) {{
            setItems("LPPPL", new HashMap<>(){{
                put('L', new ItemStack(Material.STRING));
                put('P', items.getItem("oak_plank"));
            }});
            recipes.add(this);
        }};

        new CarpenterRecipe(NamespacedKey.minecraft("boat"), new ItemStack(Material.OAK_BOAT)){{
            setItems("PPBPP", new HashMap<>(){{
                put('P', items.getItem("oak_plank"));
                put('B', items.getItem("boat_bottom"));
            }});
            recipes.add(this);
        }};

        new ShapedRecipe(NamespacedKey.minecraft("carpenter_table"), items.getItem("carpenter_table")){{
            shape("___", "ABC", "_D_");
            setIngredient('A', Material.WHITE_DYE);
            setIngredient('B', Material.PAPER);
            setIngredient('C', Material.BLUE_DYE);
            setIngredient('D', Material.CRAFTING_TABLE);
            Bukkit.addRecipe(this);
        }};

        new ShapedRecipe(NamespacedKey.minecraft("flippers_recipe"), items.getItem("flippers")){{
            shape("___", "ABA", "ACA");
            setIngredient('A', Material.KELP);
            setIngredient('B', Material.LEATHER_BOOTS);
            setIngredient('C', Material.SLIME_BALL);
            Bukkit.addRecipe(this);
        }};

        new ShapedRecipe(NamespacedKey.minecraft("oxygen_tank_light_recipe"), items.getItem("oxygen_tank_light")){{
           shape("_A_", "BCB", "_B_");
           setIngredient('A', Material.NETHERITE_SCRAP);
           setIngredient('B', items.getItem("titanium_ingot"));
           setIngredient('C', Material.YELLOW_DYE);
           Bukkit.addRecipe(this);
        }};

        new ShapedRecipe(NamespacedKey.minecraft("oxygen_tank_recipe"), items.getItem("oxygen_tank")){{
            shape("_A_", "BCB", "___");
            setIngredient('A', Material.NETHERITE_SCRAP);
            setIngredient('B', items.getItem("oxygen_tank_light"));
            setIngredient('C', items.getItem("titanium_ingot"));
            Bukkit.addRecipe(this);
        }};

        new FurnaceRecipe(NamespacedKey.minecraft("titanium_ingot_recipe"), items.getItem("titanium_ingot"), items.getItem("raw_titanium").getType(), 5, 300){{
            Bukkit.addRecipe(this);
        }};
    }
}
