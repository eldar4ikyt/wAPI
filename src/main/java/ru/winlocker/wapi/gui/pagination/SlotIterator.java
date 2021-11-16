package ru.winlocker.wapi.gui.pagination;

import lombok.*;
import ru.winlocker.wapi.gui.GuiContents;
import ru.winlocker.wapi.gui.item.GuiItem;

@RequiredArgsConstructor
@Getter
@Builder
public class SlotIterator {

    @NonNull
    private final GuiContents contents;

    @NonNull
    @Setter
    private Integer posX, posY;

    @Setter
    @NonNull
    private Boolean override;

    public SlotIterator previous() {
        if(!(this.posX == 0 && this.posY == 0)) {
            do {
                this.posX--;

                if(this.posX == 0) {
                    this.posX = 9 - 1;
                    this.posY--;
                }
            }
            while (!canPlace() && (this.posY != 0 || this.posX != 0));
        }
        return this;
    }

    public SlotIterator next() {
        if(!isEnded()) {
            do {
                this.posX = ++this.posX % 9;

                if(this.posX == 0)
                    this.posY++;
            }
            while (!canPlace() && !isEnded());
        }
        return this;
    }

    public void setItem(GuiItem guiItem) {
        this.contents.setItem(this.contents.getSlot(this.posX, this.posY), guiItem);
    }

    public boolean isEnded() {
        return (this.posY == contents.getRows() - 1 && this.posX == 9 - 1);
    }

    public boolean canPlace() {
        return this.override || contents.getItem(this.posX, this.posY) == null;
    }
}
