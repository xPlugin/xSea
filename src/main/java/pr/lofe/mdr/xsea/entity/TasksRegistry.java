package pr.lofe.mdr.xsea.entity;

import org.bukkit.entity.Player;
import pr.lofe.mdr.xsea.config.Config;
import pr.lofe.mdr.xsea.xSea;

import java.util.ArrayList;
import java.util.List;

public class TasksRegistry {

    private final List<PlayerTask> tasks = new ArrayList<>();
    private final Config data = xSea.data;

    private static TasksRegistry get() {
        return xSea.getTasks();
    }

    public static void complete(PlayerTask task, Player player) {
        Config data = get().data;

        List<PlayerTask> allTasks = get().tasks;
        List<String> tasks = data.getConfig().getStringList(player.getName() + ".tasks.completed");
        tasks.add(task.key().toString());
        data.getConfig().set(player.getName() + ".tasks.completed", tasks);

        int index = allTasks.indexOf(task) + 1;
        if(index <= allTasks.size() - 1) data.getConfig().set(player.getName() + ".tasks.current", allTasks.get(index));
        else data.getConfig().set(player.getName() + ".tasks.current", null);

        data.save();
    }

    public static void add(PlayerTask task) {
        for(PlayerTask temp: get().tasks) {
            if(temp.key().toString().equals(task.key().toString())) return;
        }
        get().tasks.add(task);

    }

    public static PlayerTask getCompleting(Player player) {
        Config data = get().data;
        String key = data.getConfig().getString(player.getName() + ".tasks.current");
        if(key == null) {
            List<String> tasks = data.getConfig().getStringList(player.getName() + ".tasks.completed");
            if(tasks.isEmpty()) {
                PlayerTask first = get().tasks.get(0);
                data.getConfig().set(player.getName() + ".tasks.current", first.key().toString());
                data.save();
                return first;
            }
            else return null;
        }


        for (PlayerTask task: get().tasks) {
            if(task.key().toString().equals(key)) return task;
        }
        return null;
    }


}
