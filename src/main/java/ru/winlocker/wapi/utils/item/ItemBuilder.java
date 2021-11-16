package ru.winlocker.wapi.utils.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import ru.winlocker.wapi.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public class ItemBuilder
{
    private ItemStack item;
    
    public static ItemBuilder loadItemBuilder(final FileConfiguration config, final String path) {

        if (config.getString(path) == null) {
            return builder(Material.AIR);
        }

        final Material material = Material.valueOf(config.getString(String.valueOf(path) + ".type").toUpperCase());
        final ItemBuilder builder = new ItemBuilder(material);
        final String displayName = config.getString(String.valueOf(path) + ".title");
        final List<String> lore = (List<String>)config.getStringList(String.valueOf(path) + ".lore");
        builder.setDurability((short)config.getInt(String.valueOf(path) + ".data"));
        if (displayName != null) {
            builder.setDisplayName(displayName);
        }
        if (lore != null) {
            builder.setLore(lore);
        }
        builder.setAmount((config.getInt(String.valueOf(path) + ".amount") > 0) ? config.getInt(String.valueOf(path) + ".amount") : 1);
        if (config.getString(String.valueOf(path) + ".enchants") != null) {
            for (final String enchants : config.getStringList(String.valueOf(path) + ".enchants")) {
                final String[] args = enchants.split(":");
                builder.enchant(Enchantment.getByName(args[0].toUpperCase()), Integer.valueOf(args[1]));
            }
        }
        for (final String flags : config.getStringList(String.valueOf(path) + ".flags")) {
            builder.flag(ItemFlag.valueOf(flags.toUpperCase()));
        }
        if (config.getString(String.valueOf(path) + ".potion-color") != null) {
            try {
                final Color color = (Color)Color.class.getField(config.getString(String.valueOf(path) + ".potion-color").toUpperCase()).get(null);
                builder.setPotionColor(color);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (config.getStringList(String.valueOf(path) + ".potion-effects") != null) {
            for (final String s : config.getStringList(String.valueOf(path) + ".potion-effects")) {
                final PotionEffectType type = PotionEffectType.getByName(s.split(":")[0].toUpperCase());
                final int level = Integer.parseInt(s.split(":")[1]) - 1;
                final int duration = Integer.parseInt(s.split(":")[2]) * 20;
                builder.addPotionEffect(new PotionEffect(type, duration, level));
            }
        }
        if (config.getString(String.valueOf(path) + ".texture") != null) {
            builder.setSkullTexture(config.getString(String.valueOf(path) + ".texture"));
        }
        return builder;
    }
    
    public static ItemBuilder builder(ItemStack item) {
        return new ItemBuilder(item);
    }
    
    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(material);
    }
    
    public ItemBuilder(final Material material) {
        this.item = new ItemStack(material);
    }
    
    public ItemBuilder(final ItemStack item) {
        this.item = item;
    }
    
    public ItemBuilder setAmount(final int amount) {
        this.item.setAmount(amount);
        return this;
    }
    
    public ItemBuilder setDurability(final short durability) {
        this.item.setDurability(durability);
        return this;
    }
    
    public ItemBuilder enchant(final Enchantment enchantment, final int level) {
        final ItemMeta meta = this.item.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder enchantall(final int level) {
        Enchantment[] values;
        for (int length = (values = Enchantment.values()).length, i = 0; i < length; ++i) {
            final Enchantment enchantment = values[i];
            this.enchant(enchantment, level);
        }
        return this;
    }
    
    public ItemBuilder flags(final List<String> flags) {
        if (flags == null) {
            return this;
        }
        for (final String flag : flags) {
            this.flag(ItemFlag.valueOf(flag.toUpperCase()));
        }
        return this;
    }
    
    public ItemBuilder flag(final ItemFlag flag) {
        final ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(flag);
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder flagall() {
        ItemFlag[] values;
        for (int length = (values = ItemFlag.values()).length, i = 0; i < length; ++i) {
            final ItemFlag flag = values[i];
            this.flag(flag);
        }
        return this;
    }
    
    public ItemBuilder setDisplayName(final String name) {
        if (name == null) {
            return this;
        }
        final ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(Utils.color(name));
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder replaceDisplayName(final String replace, final String to) {
        final ItemMeta meta = this.item.getItemMeta();
        if (!meta.hasDisplayName()) {
            return this;
        }
        meta.setDisplayName(Utils.color(meta.getDisplayName().replace(replace, to)));
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder setLore(final List<String> lore) {
        if (lore == null) {
            return this;
        }
        final ItemMeta meta = this.item.getItemMeta();
        meta.setLore(Utils.color(lore));
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder replaceLore(final String replace, final String to) {
        final ItemMeta meta = this.item.getItemMeta();
        if (!meta.hasLore()) {
            return this;
        }
        final List<String> lore = (List<String>)meta.getLore().stream().map(x -> x.replace(replace, to)).collect(Collectors.toList());
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder replace(final String replace, final String to) {
        this.replaceDisplayName(replace, Utils.color(to));
        this.replaceLore(replace, Utils.color(to));
        return this;
    }
    
    public ItemBuilder addLore(final String line) {
        final ItemMeta meta = this.item.getItemMeta();
        List<String> list;
        if (meta.hasLore()) {
            list = (List<String>)meta.getLore();
        }
        else {
            list = new ArrayList<String>();
        }
        list.add(Utils.color(line));
        meta.setLore(list);
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder removeLore(final int page) {
        final ItemMeta meta = this.item.getItemMeta();
        if (!meta.hasLore()) {
            return this;
        }
        final List<String> list = (List<String>)meta.getLore();
        if (page > list.size()) {
            return this;
        }
        list.remove(page);
        meta.setLore(Utils.color(list));
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder setPotionColor(final Color color) {
        if (!(this.item.getItemMeta() instanceof PotionMeta)) {
            return this;
        }
        final PotionMeta meta = (PotionMeta)this.item.getItemMeta();
        meta.setColor(color);
        this.item.setItemMeta((ItemMeta)meta);
        return this;
    }
    
    public ItemBuilder addPotionEffect(final PotionEffect effect) {
        if (!(this.item.getItemMeta() instanceof PotionMeta)) {
            return this;
        }
        final PotionMeta meta = (PotionMeta)this.item.getItemMeta();
        meta.addCustomEffect(effect, true);
        this.item.setItemMeta((ItemMeta)meta);
        return this;
    }
    
    public ItemBuilder setSkullTexture(final String texture) {
        if (!(this.item.getItemMeta() instanceof SkullMeta)) {
            return this;
        }
        final SkullMeta meta = (SkullMeta)this.item.getItemMeta();
        final GameProfile profile = new GameProfile(UUID.randomUUID(), (String)null);

        profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/" + texture + "\"}}}")));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.item.setItemMeta(meta);

        return this;
    }
    
    public ItemStack build() {
        return this.item;
    }
}
