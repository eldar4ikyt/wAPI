package ru.winlocker.wapi.utils.lang;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LegacyPreAction
{
    private final String packageVersion;
    
    public LegacyPreAction(final Player player, final String message) throws ClassNotFoundException {
        this.packageVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        try {
            final Object chatComponentText = this.getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(message);
            final Object chatMessageType = this.getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
            final Object packetPlayOutChat = this.getNMSClass("PacketPlayOutChat").getConstructor(this.getNMSClass("IChatBaseComponent"), this.getNMSClass("ChatMessageType")).newInstance(chatComponentText, chatMessageType);
            final Object getHandle = player.getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(player, new Object[0]);
            final Object playerConnection = getHandle.getClass().getField("playerConnection").get(getHandle);
            playerConnection.getClass().getMethod("sendPacket", this.getNMSClass("Packet")).invoke(playerConnection, packetPlayOutChat);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Class<?> getNMSClass(final String nmsClassName) {
        try {
            return Class.forName("net.minecraft.server." + this.packageVersion + "." + nmsClassName);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
