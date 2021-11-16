package ru.winlocker.wapi.gui;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.winlocker.wapi.gui.item.GuiItem;
import ru.winlocker.wapi.gui.pagination.Pagination;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Getter
public class GuiContents {

    private final @NonNull Gui gui;
    private final @NonNull Player player;

    @Getter(AccessLevel.NONE)
    private final Pagination pagination = new Pagination(this);

    @Setter private Function<Player, String> title = player -> "Gui Inventory";
    @Setter private Integer rows = 6;

    @NonNull
    private final Map<Integer, GuiItem> items = new HashMap<>();

    public GuiContents(@NonNull Gui gui, @NonNull Player player) {
        this.gui = gui;
        this.player = player;
    }

    public Pagination pagination() {
        return this.pagination;
    }

    public void setItem(int x, int y, GuiItem guiItem) {
        this.setItem(getSlot(x, y), guiItem);
    }

    public void setItem(int slot, GuiItem guiItem) {
        this.items.put(slot, guiItem);
    }

    public int getSlot(int x, int y) {
        return (y > 0 ? y - 1 : 0) * 9 + (x > 0 ? x - 1 : 0);
    }

    public GuiItem getItem(ItemStack itemStack) {

        if(itemStack == null || itemStack.getType() == Material.AIR)
            return null;

        NBTItem nbtItem = new NBTItem(itemStack);

        if(!nbtItem.hasKey("wapi-guiItem"))
            return null;

        UUID uuid = UUID.fromString(nbtItem.getString("wapi-guiItem"));

        return this.items.values().stream().filter(guiItem -> guiItem.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public GuiItem getItem(int x, int y) {
        return getItem(getSlot(x, y));
    }

    public GuiItem getItem(int slot) {
        return this.items.get(slot);
    }

    public void removeItem(int slot) {
        this.items.remove(slot);
    }

    public void fillWindow(GuiItem guiItem) {
        this.fill(0, 9, 0, this.rows, guiItem);
    }

    public void fill(int fromX, int toX, int fromY, int toY, GuiItem guiItem) {
        for (int y = fromY; y <= toY; y++) {
            for (int x = fromX; x <= toX; x++) {
                if(y == fromY || y == toY || x == fromX || x == toX) {
                    this.setItem(x, y, guiItem);
                }
            }
        }
    }
}
