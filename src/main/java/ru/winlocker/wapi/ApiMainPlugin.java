package ru.winlocker.wapi;

import org.bukkit.plugin.java.JavaPlugin;

import ru.winlocker.wapi.gui.listener.GuiListener;

public class ApiMainPlugin extends JavaPlugin {

    private static ApiMainPlugin instance;

    public static ApiMainPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        //getServer().getPluginManager().registerEvents(new PlayerListener(), this);

    }
}
