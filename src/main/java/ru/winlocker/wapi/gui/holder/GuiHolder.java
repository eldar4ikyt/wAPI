package ru.winlocker.wapi.gui.holder;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.winlocker.wapi.gui.GuiContents;
import ru.winlocker.wapi.gui.item.GuiItem;
import ru.winlocker.wapi.utils.Utils;

import java.util.Map;

@Getter
public class GuiHolder implements InventoryHolder {

    private final @NonNull Player player;
    private final @NonNull Inventory inventory;
    private final @NonNull GuiContents contents;
    
    public GuiHolder(@NonNull Player player, @NonNull GuiContents contents) {
        this.player = player;
        this.contents = contents;

        String title = Utils.color(contents.getTitle().apply(player));
        int rows = contents.getRows() * 9;

        this.inventory = Bukkit.createInventory(this, rows, title);

        this.updateContents();
    }

    public void updateContents() {
        for (Map.Entry<Integer, GuiItem> entry : contents.getItems().entrySet()) {
            GuiItem guiItem = entry.getValue();

            if(guiItem.isVisible()) {
                ItemStack wrappedItemStack = entry.getValue().getItemPlaceholders(player);

                this.inventory.setItem(entry.getKey(), wrappedItemStack);
            }
        }
    }
}
