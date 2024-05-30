package pr.lofe.mdr.xsea.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pr.lofe.mdr.xsea.xSea;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class Config {
    final String path;

    public Config(String path, boolean isResource) {
        this.file = new File(xSea.I.getDataFolder(),  path + ".yml");
        this.path = path;
        try {
            if(xSea.I.getDataFolder().isDirectory()) {

                if (!this.file.exists() && isResource)
                    xSea.I.saveResource(path + ".yml", false);
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
