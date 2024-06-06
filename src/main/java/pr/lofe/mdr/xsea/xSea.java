package pr.lofe.mdr.xsea;

import dev.jorel.commandapi.CommandAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import pr.lofe.lib.xbase.text.TextWrapper;
import pr.lofe.mdr.xsea.command.SeaCommand;
import pr.lofe.mdr.xsea.inv.CarpenterRecipe;
import pr.lofe.mdr.xsea.inv.InventoryListener;
import pr.lofe.mdr.xsea.item.ItemRegistry;
import pr.lofe.mdr.xsea.item.WaterResistance;
import pr.lofe.mdr.xsea.listener.BlockListener;
import pr.lofe.mdr.xsea.listener.ItemListener;
import pr.lofe.mdr.xsea.registry.RecipesProvider;
import pr.lofe.mdr.xsea.util.EnchantHandler;

import java.util.HashMap;

public class xSea extends JavaPlugin {

    public static xSea I;
    public Enchantment WATER_RESISTANCE;

    private RecipesProvider recipes;
    private ItemRegistry items;


    @Override public void onEnable() {
        Bukkit.broadcast(TextWrapper.text("<blue>[xSea]</blue> [patched]<br>Debug mode <u><green>enabled</green></u>, saving StackTrace`s to log file."));

        I = this;

        reloadData();

        EnchantHandler.unfreezeRegistry();
        WATER_RESISTANCE = Registry.register(BuiltInRegistries.ENCHANTMENT, "water_resistance", new WaterResistance(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
        EnchantHandler.freezeRegistry();

        ItemStack oak_plank = items.getItem("oak_plank");
        oak_plank.setAmount(8);
        CarpenterRecipe plank = new CarpenterRecipe(NamespacedKey.minecraft("plank"), oak_plank){{
            setItems("PAAAP", new HashMap<>(){{
                put('P', new ItemStack(Material.OAK_PLANKS));
                put('A', null);
            }});
        }};
        recipes.add(plank);

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
            setIngredient('_', Material.AIR);
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
}