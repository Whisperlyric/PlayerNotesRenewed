package dev.wsplrc.playernotesrenewed.client.gui.screen;

import dev.wsplrc.playernotesrenewed.client.config.NoteListManager;
import dev.wsplrc.playernotesrenewed.client.objects.NoteList;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
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

        listWidget = new PlayerListsWidget(this.minecraft, this.width, this.height - 80, 30, 25, this);
        listWidget.updateSizeAndPosition(this.width, this.height - 80, 30);
        this.addRenderableWidget(listWidget);

        int buttonWidth = 60;
        int btnCount = 6;
        int totalWidth = buttonWidth * btnCount + 10 * (btnCount - 1);
        int startX = (this.width - totalWidth) / 2;
        int buttonY = this.height - 40;

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.add_list"),
                (button) -> {
                    NoteList newList = new NoteList("New List");
                    newList.setPriority(NoteListManager.getNoteLists().size());
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new PlayerListEditScreen(this, newList, true));
                    }
                })
                .size(buttonWidth, 20)
                .pos(startX, buttonY)
                .build());

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

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.copy"),
                (button) -> {
                    NoteList selected = listWidget.getSelectedList();
                    if (selected != null && this.minecraft != null) {
                        this.minecraft.setScreen(new CopyListScreen(this, selected));
                    }
                })
                .size(buttonWidth, 20)
                .pos(startX + (buttonWidth + 10) * 2, buttonY)
                .build());

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
                .pos(startX + (buttonWidth + 10) * 3, buttonY)
                .build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.global_config"),
                (button) -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(MidnightConfig.getScreen(this, "playernotes"));
                    }
                })
                .size(buttonWidth, 20)
                .pos(startX + (buttonWidth + 10) * 4, buttonY)
                .build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.done"),
                (button) -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(this.parent);
                    }
                })
                .size(buttonWidth, 20)
                .pos(startX + (buttonWidth + 10) * 5, buttonY)
                .build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        listWidget.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        
        context.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    public static class CopyListScreen extends Screen {
        private final Screen parent;
        private final NoteList sourceList;
        private EditBox nameField;

        public CopyListScreen(Screen parent, NoteList sourceList) {
            super(Component.translatable("playernotes.gui.title.copy_list"));
            this.parent = parent;
            this.sourceList = sourceList;
        }

        @Override
        protected void init() {
            super.init();

            int centerX = this.width / 2;
            int fieldWidth = 200;

            nameField = new EditBox(this.font, centerX - fieldWidth / 2, 50, fieldWidth, 20, Component.translatable("playernotes.gui.label.new_list_name"));
            nameField.setValue(sourceList.getName() + " (Copy)");
            this.addRenderableWidget(nameField);

            this.addRenderableWidget(Button.builder(
                    Component.translatable("playernotes.gui.button.save"),
                    (button) -> {
                        String newName = nameField.getValue().trim();
                        if (!newName.isEmpty() && !NoteListManager.listNameExists(newName)) {
                            NoteList copy = sourceList.copy();
                            copy.setName(newName);
                            copy.setPriority(NoteListManager.getNoteLists().size());
                            NoteListManager.addNoteList(copy);
                            if (this.minecraft != null) {
                                this.minecraft.setScreen(parent);
                            }
                        }
                    })
                    .size(80, 20)
                    .pos(centerX - 85, 80)
                    .build());

            this.addRenderableWidget(Button.builder(
                    Component.translatable("playernotes.gui.button.cancel"),
                    (button) -> {
                        if (this.minecraft != null) {
                            this.minecraft.setScreen(parent);
                        }
                    })
                    .size(80, 20)
                    .pos(centerX + 5, 80)
                    .build());
        }

        @Override
        public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
            context.drawString(this.font, Component.translatable("playernotes.gui.label.new_list_name").getString() + ":", this.width / 2 - 100, 40, 0xAAAAAA);
        }
    }
}
