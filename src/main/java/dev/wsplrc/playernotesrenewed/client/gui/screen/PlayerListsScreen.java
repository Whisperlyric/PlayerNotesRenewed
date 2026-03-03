package dev.wsplrc.playernotesrenewed.client.gui.screen;

import dev.wsplrc.playernotesrenewed.client.config.NoteListManager;
import dev.wsplrc.playernotesrenewed.client.objects.NoteList;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PlayerListsScreen extends Screen {
    private final Screen parent;
    private PlayerListsWidget listWidget;

    public PlayerListsScreen(Screen parent) {
        super(Component.translatable("playernotes.gui.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        // List widget
        listWidget = new PlayerListsWidget(this.minecraft, this.width, this.height - 80, 30, 25, this);
        this.addRenderableWidget(listWidget);

        int buttonWidth = 70;
        int startX = this.width / 2 - (buttonWidth * 5 + 40) / 2;
        int buttonY = this.height - 40;

        // Add List button
        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.add_list"),
                (button) -> {
                    NoteList newList = new NoteList("New List");
                    newList.setPriority(NoteListManager.getNoteLists().size());
                    NoteListManager.addNoteList(newList);
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new PlayerListEditScreen(this, newList));
                    }
                })
                .size(buttonWidth, 20)
                .pos(startX, buttonY)
                .build());

        // Edit button
        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.edit"),
                (button) -> {
                    NoteList selected = listWidget.getSelectedList();
                    if (selected != null && this.minecraft != null) {
                        this.minecraft.setScreen(new PlayerListEditScreen(this, selected));
                    }
                })
                .size(buttonWidth, 20)
                .pos(startX + buttonWidth + 10, buttonY)
                .build());

        // Delete button with confirmation
        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.delete"),
                (button) -> {
                    NoteList selected = listWidget.getSelectedList();
                    if (selected != null && this.minecraft != null) {
                        this.minecraft.setScreen(new ConfirmScreen(
                                (confirm) -> {
                                    if (confirm) {
                                        NoteListManager.removeNoteList(selected);
                                        listWidget.updateList();
                                    }
                                    this.minecraft.setScreen(this);
                                },
                                Component.translatable("playernotes.gui.confirm.delete.title"),
                                Component.translatable("playernotes.gui.confirm.delete.message", selected.getName())
                        ));
                    }
                })
                .size(buttonWidth, 20)
                .pos(startX + (buttonWidth + 10) * 2, buttonY)
                .build());

        // Global Config button
        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.global_config"),
                (button) -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(MidnightConfig.getScreen(this, "playernotes"));
                    }
                })
                .size(buttonWidth, 20)
                .pos(startX + (buttonWidth + 10) * 3, buttonY)
                .build());

        // Done button
        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.done"),
                (button) -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(this.parent);
                    }
                })
                .size(buttonWidth, 20)
                .pos(startX + (buttonWidth + 10) * 4, buttonY)
                .build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        listWidget.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        
        // Title
        context.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
    }
}
