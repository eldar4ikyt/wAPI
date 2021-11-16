package ru.winlocker.wapi.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Cooldown {

    private static Map<String, Long> cooldowns = new HashMap<>();

    public static void addCooldown(Player player, String key, int seconds) {
        long delay = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.put(player.getName() + key, delay);
    }

    public static boolean hasCooldown(Player player, String key) {
        if(cooldowns.containsKey(player.getName() + key)) {
            if(cooldowns.get(player.getName() + key) > System.currentTimeMillis())
                return true;

            cooldowns.remove(player.getName() + key);
        }
        return false;
    }

    public static int getCooldown(Player player, String key) {
        return (int) ((cooldowns.get(player.getName() + key) - System.currentTimeMillis()) / 1000L);
    }
}
