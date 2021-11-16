package ru.winlocker.wapi.gui.item;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.winlocker.wapi.utils.Utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
@Getter
public class GuiItem {

    private final UUID uuid = UUID.randomUUID();

    @NonNull
    private final ItemStack item;

    @Setter
    private Consumer<InventoryClickEvent> action;

    @Setter
    private boolean visible = true;

    @Singular
    private final Map<String, Function<Player, String>> placeholders;

    public GuiItem(@NonNull ItemStack item) {
        this(item, null);
    }

    public GuiItem(@NonNull ItemStack item, Consumer<InventoryClickEvent> action) {
        this(item, action, true);
    }

    public GuiItem(@NonNull ItemStack item, Consumer<InventoryClickEvent> action, boolean visible) {
        this(item, action, visible, new HashMap<>());
    }

    public GuiItem(@NonNull ItemStack item, Consumer<InventoryClickEvent> action, boolean visible, Map<String, Function<Player, String>> placeholders) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("wapi-guiItem", this.uuid.toString());

        this.item = nbtItem.getItem();

        this.action = action;
        this.placeholders = placeholders;
    }

    public ItemStack getItemPlaceholders(Player player) {
        return getItemPlaceholders(player, this.placeholders);
    }

    public ItemStack getItemPlaceholders(Player player, Map<String, Function<Player, String>> placeholders) {
        ItemStack itemStack = this.item.clone();

        if (!placeholders.isEmpty()) {
            ItemMeta meta = itemStack.getItemMeta();

            if (meta.hasLore()) {
                List<String> lore = meta.getLore();

                meta.setLore(lore.stream().map(line -> {

                    for (String placeholder : placeholders.keySet()) {
                        if (line.contains(placeholder)) {
                            String result = placeholders.get(placeholder).apply(player);

                            line = line.replace(placeholder, result);
                        }
                    }

                    return Utils.color(line);

                }).collect(Collectors.toList()));
            }

            if(meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();

                for (String placeholder : placeholders.keySet()) {
                    if(displayName.contains(placeholder)) {
                        String result = placeholders.get(placeholder).apply(player);

                        displayName = displayName.replace(placeholder, result);
                    }
                }

                meta.setDisplayName(Utils.color(displayName));
            }

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }
}
