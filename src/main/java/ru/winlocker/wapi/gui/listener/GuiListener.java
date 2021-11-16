package ru.winlocker.wapi.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import ru.winlocker.wapi.ApiMainPlugin;
import ru.winlocker.wapi.gui.Gui;
import ru.winlocker.wapi.gui.GuiContents;
import ru.winlocker.wapi.gui.holder.GuiHolder;
import ru.winlocker.wapi.gui.item.GuiItem;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GuiListener implements Listener {

    private final Set<Gui> openedGui = new HashSet<>();
    private final ScheduledFuture<?> taskUpdate;

    public GuiListener() {
        taskUpdate = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            for (Gui gui : openedGui) {
                for (GuiHolder viewer : gui.getViewers()) {
                    viewer.updateContents();
                }
            }

        }, 1, 1, TimeUnit.SECONDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {

        if(e.getCurrentItem() != null && (e.getInventory().getHolder() instanceof GuiHolder)) {
            GuiHolder guiHolder = (GuiHolder) e.getInventory().getHolder();
            GuiContents contents = guiHolder.getContents();

            GuiItem guiItem = contents.getItem(e.getCurrentItem());

            if(guiItem != null && guiItem.getAction() != null) {
                try {
                    guiItem.getAction().accept(e);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof GuiHolder) {
            GuiHolder guiHolder = (GuiHolder) e.getInventory().getHolder();
            Gui gui = guiHolder.getContents().getGui();

            gui.getViewers().add(guiHolder);

            this.openedGui.add(gui);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof GuiHolder) {
            GuiHolder guiHolder = (GuiHolder) e.getInventory().getHolder();
            Gui gui = guiHolder.getContents().getGui();

            gui.getViewers().remove(guiHolder);

            if(gui.getViewers().isEmpty()) {
                this.openedGui.remove(gui);
            }
        }
    }

    @EventHandler
    public void onDisablePlugin(PluginDisableEvent e) {
        if(e.getPlugin().equals(ApiMainPlugin.getInstance())) {

            if(!taskUpdate.isCancelled()) {
                taskUpdate.cancel(true);
            }

            for (Gui gui : this.openedGui) {
                for (GuiHolder viewer : gui.getViewers()) {
                    viewer.getPlayer().closeInventory();
                }
            }
        }
    }
}
