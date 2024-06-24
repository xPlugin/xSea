package pr.lofe.mdr.xsea.entity;

import org.bukkit.entity.Player;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.xSea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FoodSystem {

    public enum FoodIndexState {
        POOR,
        GOOD,
        IDEAL
    }

    public static FoodIndexState getIndexState(Player player) {
        double[] indexes = getIndexes(player);
        if(indexes.length == 0) return FoodIndexState.POOR;
        for(double foodIndex: indexes) {
            if(!isFoodIndexGood(foodIndex)) return FoodIndexState.POOR;
            if(!isFoodIndexIdeal(foodIndex)) return FoodIndexState.GOOD;
        }
        return FoodIndexState.IDEAL;
    }

    public static double[] getIndexes(Player player) {
        Config data = xSea.data;
        Set<String> foodSet = new HashSet<>();
        for (int i = 7; i >= 1; i--) {
            List<String> raw = data.getConfig().getStringList(player.getName() + ".food.days." + i);
            raw.replaceAll(string -> string.split("-")[1]);
            foodSet.addAll(raw);
        }
        List<String> foodList = new ArrayList<>(foodSet);
        double[] indexes = new double[foodList.size()];
        for (int i = 0; i < foodList.size(); i++) {
            indexes[i] = getFoodIndex(player, foodList.get(i));
        }
        return indexes;
    }

    public static void addAmount(Player player, String ID) {
        Config data = xSea.data;
        List<String> foodList = data.getConfig().getStringList(player.getName() + ".food.days.temp");

        boolean isAdded = false;
        for (int i = 0; i < foodList.size(); i++) {
            String food = foodList.get(i);
            if(food.contains(ID)) {
                int amount = Integer.parseInt(food.split("-")[1]);
                amount++;
                foodList.set(i, ID + "-" + amount);
                isAdded = true;
                break;
            }
        }
        if(!isAdded) foodList.add(ID + "-1");

        data.getConfig().set(player.getName() + ".food.days.temp", foodList);
        data.save();
    }

    public static boolean isFoodIndexGood(double index) {
        return index <= 22.25;
    }

    public static boolean isFoodIndexIdeal(double index) {
        return index >= 8.25 && index <= 16.25;
    }

    public static double getMaxFoodIndex() {
        return 49.0;
    }

    public static double getFoodIndex(Player player, String id) {
        double index = 0;
        double ratio = 0.50;
        for (int i = 7; i >= 1; i--) {
            index += getFoodAmountAtDay(player, i, id) * ratio;
            ratio -= 0.05;
        }
        return index;
    }

    private static int getFoodAmountAtDay(Player player, int day, String id) {
        Config data = xSea.data;
        List<String> foodList = data.getConfig().getStringList(player.getName() + ".food.days." + day);
        for(String string: foodList) {
            if(string.contains(id)) {
                return Integer.parseInt(string.split("-")[1]);
            }
        }
        return 0;
    }

}
