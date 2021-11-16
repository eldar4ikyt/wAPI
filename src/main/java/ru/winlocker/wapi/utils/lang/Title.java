package ru.winlocker.wapi.utils.lang;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class Title
{
    public static void sendTitle(final Player player, final String text) {
        sendTitle(player, text, 15, 60, 15);
    }
    
    public static void sendTitle(final Player player, String text, final int fadein, final int stay, final int fadeout) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        final String[] args = text.split("%nl%");
        try {
            final String title = args[0];
            Object e1 = Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TIMES").get(null);
            Object chatTitle = Objects.requireNonNull(getNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMS("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
            Object titlepacket = titleConstructor.newInstance(e1, chatTitle, fadein, stay, fadeout);
            sendPacket(player, titlepacket);
            e1 = Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null);
            chatTitle = Objects.requireNonNull(getNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + title + "\"}");
            titleConstructor = Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMS("IChatBaseComponent"));
            titlepacket = titleConstructor.newInstance(e1, chatTitle);
            sendPacket(player, titlepacket);
            if (args.length == 2) {
                final String subtitle = args[1];
                Object e2 = Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TIMES").get(null);
                Object chatSubtitle = Objects.requireNonNull(getNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + subtitle + "\"}");
                Constructor<?> subtitleConstructor = Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMS("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                Object subtitlepacket = subtitleConstructor.newInstance(e2, chatSubtitle, fadein, stay, fadeout);
                sendPacket(player, subtitlepacket);
                e2 = Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                chatSubtitle = Objects.requireNonNull(getNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + subtitle + "\"}");
                subtitleConstructor = Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMS("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlepacket = subtitleConstructor.newInstance(e2, chatSubtitle, fadein, stay, fadeout);
                sendPacket(player, subtitlepacket);
            }
        }
        catch (Exception e3) {
            e3.printStackTrace();
        }
    }
    
    private static void sendPacket(final Player player, final Object packet) {
        try {
            final Object handle = player.getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(player, new Object[0]);
            final Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMS("Packet")).invoke(playerConnection, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Class<?> getNMS(final String name) {
        final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
