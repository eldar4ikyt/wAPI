package ru.winlocker.wapi.utils.lang;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Actionbar
{
    private static String version;
    
    static {
        final String name = Bukkit.getServer().getClass().getPackage().getName();
        Actionbar.version = name.substring(name.lastIndexOf(46) + 1);
    }
    
    public static void sendActionbar(final Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        try {
            if (Actionbar.version.equals("v1_16_R1") || Actionbar.version.equals("v1_16_R2") || Actionbar.version.equals("v1_16_R3")) {
                new PreAction(player, message);
            }
            else if (Actionbar.version.equals("v1_12_R1") || Actionbar.version.startsWith("v1_13") || Actionbar.version.startsWith("v1_14_") || Actionbar.version.startsWith("v1_15_")) {
                new LegacyPreAction(player, message);
            }
            else {
                final Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + Actionbar.version + ".entity.CraftPlayer");
                final Object p = c1.cast(player);
                final Class<?> c2 = Class.forName("net.minecraft.server." + Actionbar.version + ".PacketPlayOutChat");
                final Class<?> c3 = Class.forName("net.minecraft.server." + Actionbar.version + ".Packet");
                final Class<?> c4 = Class.forName("net.minecraft.server." + Actionbar.version + ".ChatComponentText");
                final Class<?> c5 = Class.forName("net.minecraft.server." + Actionbar.version + ".IChatBaseComponent");
                if (!Actionbar.version.equalsIgnoreCase("v1_8_R1") && !Actionbar.version.contains("v1_7_")) {
                    final Object o = c4.getConstructor(String.class).newInstance(message);
                    final Object ppoc = c2.getConstructor(c5, Byte.TYPE).newInstance(o, 2);
                    final Method getHandle = c1.getDeclaredMethod("getHandle", (Class<?>[])new Class[0]);
                    final Object handle = getHandle.invoke(p, new Object[0]);
                    final Field fieldConnection = handle.getClass().getDeclaredField("playerConnection");
                    final Object playerConnection = fieldConnection.get(handle);
                    final Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", c3);
                    sendPacket.invoke(playerConnection, ppoc);
                }
                else {
                    final Method m3 = c4.getDeclaredMethod("a", String.class);
                    final Object cbc = c5.cast(m3.invoke(c4, "{\"text\": \"" + message + "\"}"));
                    final Object ppoc2 = c2.getConstructor(c5, Byte.TYPE).newInstance(cbc, 2);
                    final Method getHandle2 = c1.getDeclaredMethod("getHandle", (Class<?>[])new Class[0]);
                    final Object handle2 = getHandle2.invoke(p, new Object[0]);
                    final Field fieldConnection2 = handle2.getClass().getDeclaredField("playerConnection");
                    final Object playerConnection2 = fieldConnection2.get(handle2);
                    final Method sendPacket2 = playerConnection2.getClass().getDeclaredMethod("sendPacket", c3);
                    sendPacket2.invoke(playerConnection2, ppoc2);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
