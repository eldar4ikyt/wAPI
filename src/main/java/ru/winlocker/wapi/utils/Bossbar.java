package ru.winlocker.wapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Function;

public class Bossbar extends BukkitRunnable {

    private int seconds;

    private final double total;
    private final BossBar bossBar;
    private final Function<Integer, String> title;
    
    public Bossbar(Function<Integer, String> title, int seconds, BarColor barColor, BarStyle barStyle) {
        this.title = title;
        this.seconds = seconds;
        this.total = seconds;

        this.bossBar = Bukkit.createBossBar(title.apply(seconds), barColor, barStyle);
        this.bossBar.setVisible(true);
    }

    public void start(Plugin plugin) {
        this.runTaskTimerAsynchronously(plugin, 0, 20L);
    }

    public BossBar getBossBar() {
        return this.bossBar;
    }

    public synchronized void cancel() {
        this.getBossBar().removeAll();

        super.cancel();
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void run() {
        if (seconds == -1) {
            this.cancel();
        }
        else {
            this.bossBar.setTitle(this.title.apply((int) this.seconds));

            double percent = this.seconds / this.total;
            this.bossBar.setProgress(percent);

            seconds -= 1;
        }
    }
}
