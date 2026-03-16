package dev.wsplrc.playernotesrenewed.client.gui.screen;

import dev.wsplrc.playernotesrenewed.client.config.NoteListManager;
import dev.wsplrc.playernotesrenewed.client.objects.NoteList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerListsWidget extends ContainerObjectSelectionList<PlayerListsWidget.Entry> {
    private final PlayerListsScreen parent;
    private NoteList selectedList;
    private final int listWidth;

    public PlayerListsWidget(Minecraft minecraft, int width, int height, int y, int itemHeight, PlayerListsScreen parent) {
        super(minecraft, width, height, y, itemHeight);
        this.parent = parent;
        this.listWidth = width - 20;
        this.centerListVertically = false;
        updateList();
    }

    @Override
    public int getRowWidth() {
        return listWidth;
    }

    @Override
    protected int scrollBarX() {
        return this.getX() + this.listWidth + 6;
    }

    public void updateList() {
        this.clearEntries();
        for (NoteList noteList : NoteListManager.getNoteLists()) {
            this.addEntry(new NoteListEntry(noteList, this));
        }
    }

    public void selectList(NoteList noteList) {
        this.selectedList = noteList;
    }

    public NoteList getSelectedList() {
        return selectedList;
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        public final List<AbstractWidget> elements = new ArrayList<>();

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return elements;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return elements;
        }
    }

    public static class NoteListEntry extends Entry {
        private final NoteList noteList;
        private final PlayerListsWidget parent;
        private final StringWidget nameWidget;
        private final StringWidget prefixWidget;
        private final StringWidget countWidget;
        private final Checkbox enabledCheckbox;

        public NoteListEntry(NoteList noteList, PlayerListsWidget parent) {
            this.noteList = noteList;
            this.parent = parent;

            String name = noteList.getName();
            if (name == null || name.isEmpty()) {
                name = "Unnamed List";
            }
            this.nameWidget = new StringWidget(0, 0, 100, 20, Component.literal(name), Minecraft.getInstance().font);
            elements.add(nameWidget);

            this.enabledCheckbox = Checkbox.builder(Component.empty(), Minecraft.getInstance().font)
                    .pos(0, 0)
                    .selected(noteList.isEnabled())
                    .onValueChange((checkbox, selected) -> {
                        noteList.setEnabled(selected);
                        NoteListManager.save();
                    })
                    .build();
            elements.add(enabledCheckbox);

            String prefix = noteList.getFormattedPrefix();
            this.prefixWidget = new StringWidget(0, 0, 150, 20, Component.literal("Prefix: " + prefix), Minecraft.getInstance().font);
            elements.add(prefixWidget);

            int playerCount = noteList.getPlayers().size();
            String countText = playerCount + " player" + (playerCount != 1 ? "s" : "");
            this.countWidget = new StringWidget(0, 0, 60, 20, Component.literal(countText), Minecraft.getInstance().font);
            elements.add(countWidget);
        }

        @Override
        public void renderContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = getContentX();
            int y = getContentY();
            int width = getContentWidth();
            int height = getContentHeight();

            if (hovered || parent.selectedList == noteList) {
                guiGraphics.fill(x, y, x + width, y + height, 0x80FFFFFF);
            }

            int statusColor = noteList.isEnabled() ? 0xFF00FF00 : 0xFFFF0000;
            guiGraphics.fill(x + 2, y + 2, x + 6, y + height - 2, statusColor);

            int centerY = y + (height - 10) / 2 - 1;
            
            nameWidget.setX(x + 12);
            nameWidget.setY(centerY);
            nameWidget.render(guiGraphics, mouseX, mouseY, tickDelta);
            
            enabledCheckbox.setX(x + 120);
            enabledCheckbox.setY(centerY - 2);
            enabledCheckbox.render(guiGraphics, mouseX, mouseY, tickDelta);
            
            prefixWidget.setX(x + 145);
            prefixWidget.setY(centerY);
            prefixWidget.render(guiGraphics, mouseX, mouseY, tickDelta);
            
            countWidget.setX(x + width - 70);
            countWidget.setY(centerY);
            countWidget.render(guiGraphics, mouseX, mouseY, tickDelta);
        }

        @Override
        public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
            parent.selectList(noteList);
            if (enabledCheckbox.isMouseOver(event.x(), event.y())) {
                return enabledCheckbox.mouseClicked(event, doubleClick);
            }
            return true;
        }
    }
}
