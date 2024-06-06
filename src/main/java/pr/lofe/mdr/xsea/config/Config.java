package pr.lofe.mdr.xsea.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pr.lofe.mdr.xsea.xSea;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Config {
    final String path;

    public Config(String path, boolean isResource, boolean replace) {
        this.file = new File(xSea.I.getDataFolder(),  path + ".yml");
        this.path = path;
        try {
            if(xSea.I.getDataFolder().isDirectory()) {

                if (!this.file.exists() && isResource)
                    xSea.I.saveResource(path + ".yml", replace);
                if (!this.file.exists() && !this.file.createNewFile())
                    throw new IOException();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create " + path, e);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public Config(File folder, String path) {
        this.file = new File(folder,  path + ".yml");
        this.path = path;
        try {
            if(xSea.I.getDataFolder().isDirectory()) {
                if (!this.file.exists() && !this.file.createNewFile())
                    throw new IOException();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create " + path, e);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public List<String> getKeys(String path) {
        if(config != null) {
            return config.getKeys(true).stream()
                    .filter(key -> key.startsWith(path))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public void save() {
        try {
            this.config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        save();
        config = null;
        file = null;
        System.gc();
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public File getFile() {
        return file;
    }

    private File file;
    private FileConfiguration config;
}
