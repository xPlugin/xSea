package pr.lofe.mdr.xsea.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author StreamVersus
 * Реализация скилла
 * Луфики кушают
 */
public class MiningSkillUtil {
    private static final List<BlockFace> lookup = new ArrayList<>(List.of(BlockFace.values()));
    /**
     * Небезопасное API, вызвать после проверки условий
     * Реализует все, кроме проверок(расчет, ломание блоков, прочность кирки)
     * @param player Игрок, прокнувший скилл
     * @param block Выкопанный блок
     */
    public static void skillProc(Player player, Block block){
        ItemStack pickaxe = player.getActiveItem();
        Set<Block> set = new HashSet<>(){{add(block);}};
        set = recursiveMethod(set, 0);
        for (Block block1 : set) {
            block1.breakNaturally(pickaxe);
        }
        Damageable dmg = (Damageable) pickaxe.getItemMeta();
        double hp = dmg.getHealth();
        hp = hp - (set.size() * 2);
        dmg.setHealth(hp);
        pickaxe.setItemMeta((ItemMeta) dmg);
    }

    //Рекурсия - my beloved
    private static Set<Block> recursiveMethod(Set<Block> set, int id){
        Block block = (Block) set.toArray()[id];
        boolean buffer = false;
        for (BlockFace blockFace : lookup) {
            buffer = true;
            Block loop = block.getRelative(blockFace);
            if(loop.getType() != Material.AIR && loop.getType() == block.getType()) set.add(loop);
        }

        if(set.size() >= 10 || buffer) {
            return set;
        }else{
            return recursiveMethod(set, id+1);
        }
    }
}
