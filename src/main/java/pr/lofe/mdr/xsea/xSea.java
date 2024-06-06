package pr.lofe.mdr.xsea;

import dev.jorel.commandapi.CommandAPI;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.command.SeaCommand;
import pr.lofe.mdr.xsea.enchant.EnchantmentHandler;
import pr.lofe.mdr.xsea.enchant.WaterResistance;
import pr.lofe.mdr.xsea.inv.CarpenterRecipe;
import pr.lofe.mdr.xsea.inv.InventoryListener;
import pr.lofe.mdr.xsea.item.ItemRegistry;
import pr.lofe.mdr.xsea.listener.*;
import pr.lofe.mdr.xsea.registry.RecipesProvider;

import java.util.HashMap;

public class xSea extends JavaPlugin {

    public static xSea I;

    private RecipesProvider recipes;
    private ItemRegistry items;

    private WaterResistance WATER_RESISTANCE;


    @Override public void onEnable() {
        Bukkit.broadcast(TextWrapper.text("<blue>[xSea]</blue> [patched]<br>Debug mode <u><green>enabled</green></u>, saving StackTrace`s to log file."));

        I = this;

        reloadData();

        ItemStack oak_plank = items.getItem("oak_plank");
        oak_plank.setAmount(8);
        CarpenterRecipe plank = new CarpenterRecipe(NamespacedKey.minecraft("plank"), oak_plank){{
            setItems("PAAAP", new HashMap<>(){{
                put('P', new ItemStack(Material.OAK_PLANKS));
                put('A', null);
            }});
        }};
        recipes.add(plank);

        WATER_RESISTANCE = new WaterResistance();
        Bukkit.getPluginManager().registerEvents(WATER_RESISTANCE, this);

        CarpenterRecipe bottom = new CarpenterRecipe(NamespacedKey.minecraft("boat_bottom"), items.getItem("boat_bottom")) {{
            setItems("LPPPL", new HashMap<>(){{
                put('L', new ItemStack(Material.STRING));
                put('P', items.getItem("oak_plank"));
            }});
        }};
        recipes.add(bottom);

        CarpenterRecipe boat = new CarpenterRecipe(NamespacedKey.minecraft("boat"), new ItemStack(Material.OAK_BOAT)){{
           setItems("PPBPP", new HashMap<>(){{
               put('P', items.getItem("oak_plank"));
               put('B', items.getItem("boat_bottom"));
           }});
        }};
        recipes.add(boat);

        ShapedRecipe recipe = new ShapedRecipe(NamespacedKey.minecraft("carpenter_table"), items.getItem("carpenter_table")){{
            shape("___", "ABC", "_D_");
            setIngredient('A', Material.WHITE_DYE);
            setIngredient('B', Material.PAPER);
            setIngredient('C', Material.BLUE_DYE);
            setIngredient('D', Material.CRAFTING_TABLE);
        }};
        Bukkit.addRecipe(recipe);

        new SeaCommand().register();
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
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
    }

    public static RecipesProvider getRecipes() {
        return I.recipes;
    }
    public static ItemRegistry getItems() { return I.items; }
    public static WaterResistance getEnchant() { return I.WATER_RESISTANCE; }
}