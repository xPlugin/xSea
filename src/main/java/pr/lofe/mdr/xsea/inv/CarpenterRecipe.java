package pr.lofe.mdr.xsea.inv;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CarpenterRecipe {

    private final NamespacedKey key;
    private ItemStack result;
    private ItemStack[] materials;

    public CarpenterRecipe(NamespacedKey key, ItemStack result) {
        this.key = key;
        this.result = result;
    }

    public ItemStack getResult() {
        return result.clone();
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public NamespacedKey key() {
        return key;
    }

    public boolean setItems(String shape, HashMap<Character, ItemStack> materials) {
        if(shape.length() != 5) return false;

        ItemStack[] pattern = new ItemStack[5];
        for (Character ch: materials.keySet()) {
            for (int i = 0; i < 5; i++) {
                if(shape.charAt(i) == ch) pattern[i] = materials.get(ch);
            }
        }
        this.materials = pattern;
        return true;
    }

    public ItemStack getRecipeItem(int index) {
        return materials[index];
    }

    public ItemStack[] getRecipeMatrix() {
        return materials;
    }

}
