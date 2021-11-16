package ru.winlocker.wapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.winlocker.wapi.utils.file.Config;
import ru.winlocker.wapi.utils.lang.Actionbar;
import ru.winlocker.wapi.utils.lang.Title;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Utils
{
    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.0");

    private static FileConfiguration config;
    
    public static FileConfiguration getConfig() {
        return (Utils.config != null) ? Utils.config : (Utils.config = Config.getFile("config.yml"));
    }
    
    public static void reloadConfig() {
        Utils.config = Config.getFile("config.yml");
    }
    
    public static String getMessage(final String path) {
        return getConfig().getString("messages." + path);
    }
    
    public static String getString(final String path) {
        return getConfig().getString(path);
    }
    
    public static List<String> getStringList(final String path) {
        return (List<String>)getConfig().getStringList(path);
    }
    
    public static int getInt(final String path) {
        return getConfig().getInt(path);
    }
    
    public static double getDouble(final String path) {
        return getConfig().getDouble(path);
    }
    
    public static boolean getBoolean(final String path) {
        return getConfig().getBoolean(path);
    }
    
    public static String color(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public static List<String> color(final List<String> text) {
        return text.stream().map(Utils::color).collect(Collectors.toList());
    }
    
    public static boolean has(final CommandSender player, final String permission) {
        if (!player.hasPermission(permission)) {
            sendMessage(player, getConfig().getString("messages.no-permission"));
            return false;
        }
        return true;
    }
    
    public static String format(final int time) {
        final int days = time / 86400;
        final int hours = time % 86400 / 3600;
        final int minutes = time % 3600 / 60;
        final int seconds = time % 60;
        final StringBuilder builder = new StringBuilder();
        if (days > 0) {
            builder.append(getString("time.days").replace("%size%", String.valueOf(days))).append(" ");
        }
        if (hours > 0) {
            builder.append(getString("time.hours").replace("%size%", String.valueOf(hours))).append(" ");
        }
        if (minutes > 0) {
            builder.append(getString("time.minutes").replace("%size%", String.valueOf(minutes))).append(" ");
        }
        if (seconds > 0) {
            builder.append(getString("time.seconds").replace("%size%", String.valueOf(seconds))).append(" ");
        }
        final String format = builder.toString().trim().isEmpty() ? getString("time.now") : builder.toString().trim();
        return color(format);
    }
    
    public static String formatDecimal(final double value) {
        return Utils.decimalFormat.format(value);
    }
    
    public static String numberFormat(final double number) {
        return numberFormat((int)number);
    }
    
    public static String numberFormat(final int number) {
        final NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        return String.valueOf(format.format(number)) + " ";
    }
    
    public static void sendMessage(final CommandSender sender, final String text) {
        sendMessage(sender, text, true);
    }
    
    public static void sendMessage(final CommandSender player, final String text, final boolean prefix) {
        if (text.isEmpty()) {
            return;
        }
        String[] split;
        for (int length = (split = text.split(";")).length, i = 0; i < length; ++i) {
            String line = split[i];
            line = line.replace("%player%", player.getName());
            if (line.startsWith("title:")) {
                if (player instanceof Player) {
                    Title.sendTitle((Player)player, line.split("title:")[1]);
                }
            }
            else if (line.startsWith("actionbar:")) {
                if (player instanceof Player) {
                    Actionbar.sendActionbar((Player)player, line.split("actionbar:")[1]);
                }
            }
            else {
                player.sendMessage(color(String.valueOf(prefix ? getMessage("prefix") : "") + line));
            }
        }
    }

    private static final List<List<String>> cache = new ArrayList<>();

    private static List<String> getCache(final String command) {
        return Utils.cache.stream().filter(list -> list.contains(command.toLowerCase())).findAny().orElse(null);
    }

    public static List<String> getAliases(final String command) {
        List<String> list = getCache(command);
        if (list != null) {
            return list;
        }
        list = new ArrayList<String>(Collections.singletonList(command));
        Plugin[] plugins;
        for (int length = (plugins = Bukkit.getPluginManager().getPlugins()).length, i = 0; i < length; ++i) {
            final Plugin plugin = plugins[i];
            final JavaPlugin javaPlugin = (JavaPlugin)plugin;
            if (javaPlugin.getCommand(command) != null && javaPlugin.getCommand(command).getAliases() != null) {
                list.addAll(javaPlugin.getCommand(command).getAliases());
            }
            if (javaPlugin.getDescription().getCommands() != null) {
                for (final Map.Entry<String, Map<String, Object>> commands : javaPlugin.getDescription().getCommands().entrySet()) {
                    if (javaPlugin.getCommand((String)commands.getKey()) != null && javaPlugin.getCommand((String)commands.getKey()).getAliases() != null && javaPlugin.getCommand((String)commands.getKey()).getAliases().stream().anyMatch(x -> x.equalsIgnoreCase(command))) {
                        list.add(commands.getKey().toLowerCase());
                    }
                }
            }
        }
        list = list.stream().map(String::toLowerCase).collect(Collectors.toList());
        Utils.cache.add(list);
        return list;
    }

    public static List<String> getAliases(final List<String> commands) {
        final List<String> list = new ArrayList<String>();
        commands.forEach(x -> list.addAll(getAliases(x)));
        return list;
    }
}
