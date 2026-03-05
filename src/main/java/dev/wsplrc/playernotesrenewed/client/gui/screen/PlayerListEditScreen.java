package dev.wsplrc.playernotesrenewed.client.gui.screen;

import dev.wsplrc.playernotesrenewed.client.config.NoteListManager;
import dev.wsplrc.playernotesrenewed.client.objects.NoteList;
import dev.wsplrc.playernotesrenewed.client.objects.PlayerEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListEditScreen extends Screen {
    private final Screen parent;
    private final NoteList noteList;
    private EditBox nameField;
    private EditBox prefixField;
    private Checkbox enabledCheckbox;
    private PlayerListPlayersWidget playersWidget;
    private EditBox addPlayerField;

    public PlayerListEditScreen(Screen parent, NoteList noteList) {
        super(Component.translatable("playernotes.gui.edit.title").append(" " + noteList.getName()));
        this.parent = parent;
        this.noteList = noteList;
    }

    @Override
    protected void init() {
        super.init();

        int leftColumnX = 10;
        int leftColumnWidth = Math.min(this.width / 2 - 30, 200);
        int rightColumnX = this.width / 2 + 10;
        int rightColumnWidth = this.width / 2 - 30;

        nameField = new EditBox(this.font, leftColumnX, 30, leftColumnWidth, 20, Component.translatable("playernotes.gui.label.list_name"));
        nameField.setValue(noteList.getName());
        this.addRenderableWidget(nameField);

        prefixField = new EditBox(this.font, leftColumnX, 60, leftColumnWidth, 20, Component.translatable("playernotes.gui.label.prefix"));
        prefixField.setValue(noteList.getPrefix());
        this.addRenderableWidget(prefixField);

        enabledCheckbox = Checkbox.builder(Component.translatable("playernotes.gui.label.enabled"), this.font)
                .pos(leftColumnX, 90)
                .selected(noteList.isEnabled())
                .build();
        this.addRenderableWidget(enabledCheckbox);

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.prefix_format_help"),
                (button) -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new PrefixFormatHelpScreen(this));
                    }
                })
                .size(Math.min(120, leftColumnWidth), 20)
                .pos(leftColumnX, 115)
                .build());

        int listHeight = this.height - 180;
        playersWidget = new PlayerListPlayersWidget(this.minecraft, rightColumnWidth, listHeight, 30, 25, noteList);
        playersWidget.updateSizeAndPosition(rightColumnWidth, listHeight, 30);
        playersWidget.setX(rightColumnX);
        this.addRenderableWidget(playersWidget);

        int bottomY = this.height - 110;
        int buttonHeight = 20;
        int smallButtonWidth = 50;

        addPlayerField = new EditBox(this.font, rightColumnX, bottomY, rightColumnWidth - smallButtonWidth - 5, 20, Component.literal("Player Name"));
        this.addRenderableWidget(addPlayerField);

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.add"),
                (button) -> addPlayer())
                .size(smallButtonWidth, buttonHeight)
                .pos(rightColumnX + rightColumnWidth - smallButtonWidth, bottomY)
                .build());

        int btnRow2Y = bottomY + 25;
        int btnCount = 4;
        int btnSpacing = 5;
        int totalBtnWidth = rightColumnWidth - (btnSpacing * (btnCount - 1));
        int btnW = totalBtnWidth / btnCount;

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.remove"),
                (button) -> removePlayer())
                .size(btnW, buttonHeight)
                .pos(rightColumnX, btnRow2Y)
                .build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.refresh_status"),
                (button) -> {
                    playersWidget.updateOnlineStatus();
                    NoteListManager.save();
                })
                .size(btnW, buttonHeight)
                .pos(rightColumnX + btnW + btnSpacing, btnRow2Y)
                .build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.move_up"),
                (button) -> {
                    NoteListManager.moveNoteListUp(noteList);
                })
                .size(btnW, buttonHeight)
                .pos(rightColumnX + (btnW + btnSpacing) * 2, btnRow2Y)
                .build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.move_down"),
                (button) -> {
                    NoteListManager.moveNoteListDown(noteList);
                })
                .size(btnW, buttonHeight)
                .pos(rightColumnX + (btnW + btnSpacing) * 3, btnRow2Y)
                .build());

        int saveCancelY = this.height - 35;
        int saveCancelWidth = 80;
        int saveCancelSpacing = 10;

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.save"),
                (button) -> saveAndClose())
                .size(saveCancelWidth, buttonHeight)
                .pos(this.width / 2 - saveCancelWidth - saveCancelSpacing / 2, saveCancelY)
                .build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.cancel"),
                (button) -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(parent);
                    }
                })
                .size(saveCancelWidth, buttonHeight)
                .pos(this.width / 2 + saveCancelSpacing / 2, saveCancelY)
                .build());

        playersWidget.updateOnlineStatus();
    }

    private void addPlayer() {
        String playerName = addPlayerField.getValue().trim();
        if (!playerName.isEmpty()) {
            UUID playerUUID = getPlayerUUID(playerName);
            if (playerUUID != null) {
                noteList.addPlayer(playerName, playerUUID);
            } else {
                noteList.addPlayer(playerName, "");
            }
            addPlayerField.setValue("");
            playersWidget.updateList();
            NoteListManager.save();
        }
    }

    private UUID getPlayerUUID(String playerName) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null) {
            for (PlayerInfo playerInfo : mc.getConnection().getOnlinePlayers()) {
                if (playerInfo.getProfile().name().equalsIgnoreCase(playerName)) {
                    return playerInfo.getProfile().id();
                }
            }
        }
        return null;
    }

    private void removePlayer() {
        PlayerEntry selectedPlayer = playersWidget.getSelectedPlayer();
        if (selectedPlayer != null) {
            noteList.removePlayer(selectedPlayer);
            playersWidget.updateList();
            NoteListManager.save();
        }
    }

    private void saveAndClose() {
        noteList.setName(nameField.getValue());
        noteList.setPrefix(prefixField.getValue());
        noteList.setEnabled(enabledCheckbox.selected());

        NoteListManager.save();

        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        context.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        context.drawString(this.font, Component.translatable("playernotes.gui.label.list_name").getString() + ":", 10, 20, 0xAAAAAA);
        context.drawString(this.font, Component.translatable("playernotes.gui.label.prefix").getString() + ":", 10, 50, 0xAAAAAA);
        context.drawString(this.font, Component.translatable("playernotes.gui.label.players").getString() + ":", this.width / 2 + 10, 20, 0xAAAAAA);

        String preview = noteList.getFormattedPrefix() + "PlayerName";
        context.drawString(this.font, Component.translatable("playernotes.gui.label.preview").getString() + ": " + preview, 10, 140, 0xFFFFFF);

        nameField.render(context, mouseX, mouseY, delta);
        prefixField.render(context, mouseX, mouseY, delta);
        addPlayerField.render(context, mouseX, mouseY, delta);
        playersWidget.render(context, mouseX, mouseY, delta);
    }

    public static class PrefixFormatHelpScreen extends Screen {
        private final Screen parent;
        private final List<StringWidget> textWidgets = new ArrayList<>();

        public PrefixFormatHelpScreen(Screen parent) {
            super(Component.translatable("playernotes.gui.title.prefix_format_help"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            super.init();
            textWidgets.clear();

            int x = 20;
            int y = 35;
            int lineHeight = 12;

            StringWidget introWidget = new StringWidget(x, y, 300, 12, 
                Component.translatable("playernotes.gui.help.format_intro"), this.font);
            textWidgets.add(introWidget);
            this.addRenderableOnly(introWidget);
            y += lineHeight + 8;

            StringWidget colorsLabel = new StringWidget(x, y, 100, 12, 
                Component.translatable("playernotes.gui.help.colors"), this.font);
            textWidgets.add(colorsLabel);
            this.addRenderableOnly(colorsLabel);
            y += lineHeight;

            String[][] colors = {
                {"&0", "Black", "0x000000"}, {"&1", "Dark Blue", "0x0000AA"}, 
                {"&2", "Dark Green", "0x00AA00"}, {"&3", "Dark Aqua", "0x00AAAA"},
                {"&4", "Dark Red", "0xAA0000"}, {"&5", "Dark Purple", "0xAA00AA"}, 
                {"&6", "Gold", "0xFFAA00"}, {"&7", "Gray", "0xAAAAAA"},
                {"&8", "Dark Gray", "0x555555"}, {"&9", "Blue", "0x5555FF"}, 
                {"&a", "Green", "0x55FF55"}, {"&b", "Aqua", "0x55FFFF"},
                {"&c", "Red", "0xFF5555"}, {"&d", "Light Purple", "0xFF55FF"}, 
                {"&e", "Yellow", "0xFFFF55"}, {"&f", "White", "0xFFFFFF"}
            };

            for (String[] color : colors) {
                StringWidget colorWidget = new StringWidget(x + 10, y, 200, 12, 
                    Component.literal(color[0] + " - " + color[1]), this.font);
                textWidgets.add(colorWidget);
                this.addRenderableOnly(colorWidget);
                y += lineHeight;
            }

            y += 5;
            StringWidget stylesLabel = new StringWidget(x, y, 100, 12, 
                Component.translatable("playernotes.gui.help.styles"), this.font);
            textWidgets.add(stylesLabel);
            this.addRenderableOnly(stylesLabel);
            y += lineHeight;

            String[][] styles = {
                {"&l", "Bold"}, {"&o", "Italic"}, {"&n", "Underline"},
                {"&m", "Strikethrough"}, {"&k", "Obfuscated"}, {"&r", "Reset"}
            };

            for (String[] style : styles) {
                StringWidget styleWidget = new StringWidget(x + 10, y, 200, 12, 
                    Component.literal(style[0] + " - " + style[1]), this.font);
                textWidgets.add(styleWidget);
                this.addRenderableOnly(styleWidget);
                y += lineHeight;
            }

            y += 8;
            StringWidget exampleWidget = new StringWidget(x, y, 300, 12, 
                Component.translatable("playernotes.gui.help.example"), this.font);
            textWidgets.add(exampleWidget);
            this.addRenderableOnly(exampleWidget);
            y += lineHeight + 5;

            StringWidget wikiWidget = new StringWidget(x, y, 350, 12, 
                Component.translatable("playernotes.gui.help.wiki"), this.font);
            textWidgets.add(wikiWidget);
            this.addRenderableOnly(wikiWidget);

            this.addRenderableWidget(Button.builder(
                    Component.translatable("playernotes.gui.button.done"),
                    (btn) -> {
                        if (this.minecraft != null) {
                            this.minecraft.setScreen(parent);
                        }
                    })
                    .size(80, 20)
                    .pos(this.width / 2 - 40, this.height - 40)
                    .build());
        }

        @Override
        public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        }
    }
}
