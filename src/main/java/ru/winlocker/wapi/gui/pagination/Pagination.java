package ru.winlocker.wapi.gui.pagination;

import lombok.*;
import ru.winlocker.wapi.gui.GuiContents;
import ru.winlocker.wapi.gui.item.GuiItem;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Pagination {

    @NonNull
    private final GuiContents contents;

    @Setter
    private int page;

    @Setter
    private List<GuiItem> items = new ArrayList<>();

    @Setter
    private int pageCountItems = 28;

    public Pagination(@NonNull GuiContents contents) {
        this.contents = contents;
    }

    public int getPages() {
        return this.items.size() / this.pageCountItems;
    }

    public void create(int fromX, int fromY) {
        this.create(fromX, fromY, false);
    }

    public void create(int fromX, int fromY, boolean override) {
        SlotIterator iterator = SlotIterator.builder().posX(fromX).posY(fromY).contents(this.contents).override(override).build();

        for (GuiItem pagedItem : this.getPagedItems()) {
            iterator.next().setItem(pagedItem);

            if(iterator.isEnded())
                break;
        }
    }

    public List<GuiItem> getPagedItems() {
        int fromIndex = this.page * this.pageCountItems;
        int toIndex = Math.min((this.page + 1) * this.pageCountItems, this.items.size());

        return this.items.subList(fromIndex, toIndex);
    }

    public void addItem(GuiItem guiItem) {
        this.items.add(guiItem);
    }

    public boolean next() {
        if(page < getPages()) {
            page = page + 1;

            this.updatePagination();

            return true;
        }
        return false;
    }

    public boolean previous() {
        if(page > 0) {
            page = page - 1;

            this.updatePagination();

            return true;
        }
        return false;
    }

    public void updatePagination() {
        this.contents.getGui().show(this.contents.getPlayer(), this.getPage());
    }
}
