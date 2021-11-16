package ru.winlocker.wapi.gui;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import ru.winlocker.wapi.gui.holder.GuiHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Builder
public class Gui {

    private static boolean hasRegistered;

    private final @NonNull GuiProvider provider;
    private final @Singular List<InventoryEvent> listeners;

    private final Set<GuiHolder> viewers = new HashSet<>();

    public void show(Player player) {
        this.show(player, 0);
    }

    public void show(Player player, int page) {
        GuiContents contents = new GuiContents(this, player);
        contents.pagination().setPage(page);

        provider.display(contents);

        GuiHolder holder = new GuiHolder(player, contents);
        player.openInventory(holder.getInventory());
    }
}
