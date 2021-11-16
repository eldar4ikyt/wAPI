package ru.winlocker.wapi.utils.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Config
{
    private static JavaPlugin instance;
    
    static {
        Config.instance = JavaPlugin.getProvidingPlugin(Config.class);
    }
    
    public static FileConfiguration getFile(final String fileName) {
        final File file = new File(Config.instance.getDataFolder(), fileName);
        if (Config.instance.getResource(fileName) == null) {
            return save((FileConfiguration)YamlConfiguration.loadConfiguration(file), fileName);
        }
        if (!file.exists()) {
            Config.instance.saveResource(fileName, false);
        }
        return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
    }
    
    public static FileConfiguration save(final FileConfiguration config, final String fileName) {
        try {
            config.save(new File(Config.instance.getDataFolder(), fileName));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
}
