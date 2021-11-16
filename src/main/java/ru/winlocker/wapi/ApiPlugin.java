package ru.winlocker.wapi;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class ApiPlugin extends JavaPlugin {

    @Override
    public final void onEnable() {
        onEnablePlugin();
    }

    @Override
    public final void onDisable() {
        onDisablePlugin();
    }

    public abstract void onEnablePlugin();
    public abstract void onDisablePlugin();
}
