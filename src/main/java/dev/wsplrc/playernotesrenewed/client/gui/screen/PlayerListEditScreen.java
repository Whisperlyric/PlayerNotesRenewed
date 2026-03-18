package dev.wsplrc.playernotesrenewed.client.gui.screen;

import dev.wsplrc.playernotesrenewed.client.config.NoteListManager;
import dev.wsplrc.playernotesrenewed.client.objects.NoteList;
import dev.wsplrc.playernotesrenewed.client.objects.PlayerEntry;
import dev.wsplrc.playernotesrenewed.client.objects.StyleMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListEditScreen extends Screen {
    private final Screen parent;
    private final NoteList noteList;
    private final boolean isNewList;
    private String currentName;
    private StyleMode currentStyleMode;
    private String currentStyleText;
    private boolean currentEnabled;
    private EditBox nameField;
    private EditBox styleTextField;
    private Button styleModeBtn;
    private Checkbox enabledCheckbox;
    private PlayerListPlayersWidget playersWidget;
    private EditBox addPlayerField;
    private StringWidget feedbackWidget;
    private long feedbackTime;

    public PlayerListEditScreen(Screen parent, NoteList noteList) {
        this(parent, noteList, false);
    }

    public PlayerListEditScreen(Screen parent, NoteList noteList, boolean isNewList) {
        super(Component.translatable("playernotes.gui.edit.title").append(" " + noteList.getName()));
        this.parent = parent;
        this.noteList = noteList;
        this.isNewList = isNewList;
        this.currentName = noteList.getName();
        this.currentStyleMode = noteList.getStyleMode();
        this.currentStyleText = noteList.getStyleText();
        this.currentEnabled = noteList.isEnabled();
    }

    @Override
    protected void init() {
        super.init();

        int leftColumnX = 10;
        int leftColumnWidth = Math.min(this.width / 2 - 30, 200);
        int rightColumnX = this.width / 2 + 10;
        int rightColumnWidth = this.width / 2 - 30;

        int yPos = 20;

        StringWidget nameLabel = new StringWidget(leftColumnX, yPos, leftColumnWidth, 12, 
            Component.translatable("playernotes.gui.label.list_name"), this.font);
        this.addRenderableOnly(nameLabel);
        yPos += 14;

        nameField = new EditBox(this.font, leftColumnX, yPos, leftColumnWidth, 18, Component.translatable("playernotes.gui.label.list_name"));
        nameField.setValue(currentName);
        this.addRenderableWidget(nameField);
        yPos += 24;

        StringWidget styleModeLabel = new StringWidget(leftColumnX, yPos, leftColumnWidth, 12, 
            Component.translatable("playernotes.gui.label.style_mode"), this.font);
        this.addRenderableOnly(styleModeLabel);
        yPos += 14;

        styleModeBtn = Button.builder(getStyleModeLabel(currentStyleMode), (button) -> {
            cycleStyleMode();
        }).size(leftColumnWidth, 18).pos(leftColumnX, yPos).build();
        this.addRenderableWidget(styleModeBtn);
        yPos += 24;

        StringWidget styleTextLabel = new StringWidget(leftColumnX, yPos, leftColumnWidth, 12, 
            getStyleTextLabel(currentStyleMode), this.font);
        this.addRenderableOnly(styleTextLabel);
        yPos += 14;

        styleTextField = new EditBox(this.font, leftColumnX, yPos, leftColumnWidth, 18, Component.translatable("playernotes.gui.label.style_text"));
        styleTextField.setValue(currentStyleText);
        this.addRenderableWidget(styleTextField);
        yPos += 24;

        enabledCheckbox = Checkbox.builder(Component.translatable("playernotes.gui.label.enabled"), this.font)
                .pos(leftColumnX, yPos)
                .selected(currentEnabled)
                .build();
        this.addRenderableWidget(enabledCheckbox);
        yPos += 24;

        this.addRenderableWidget(Button.builder(
                Component.translatable("playernotes.gui.button.prefix_format_help"),
                (button) -> {
                    saveCurrentState();
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new PrefixFormatHelpScreen(this));
                    }
                })
                .size(Math.min(120, leftColumnWidth), 18)
                .pos(leftColumnX, yPos)
                .build());

        int listHeight = this.height - 160;
        playersWidget = new PlayerListPlayersWidget(this.minecraft, rightColumnWidth, listHeight, 20, 25, noteList);
        playersWidget.updateSizeAndPosition(rightColumnWidth, listHeight, 20);
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

        feedbackWidget = new StringWidget(rightColumnX, bottomY - 12, rightColumnWidth, 12, Component.empty(), this.font);
        this.addRenderableOnly(feedbackWidget);

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
                    if (isNewList) {
                        NoteListManager.removeNoteList(noteList);
                    }
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(parent);
                    }
                })
                .size(saveCancelWidth, buttonHeight)
                .pos(this.width / 2 + saveCancelSpacing / 2, saveCancelY)
                .build());

        playersWidget.updateOnlineStatus();
    }

    private Component getStyleModeLabel(StyleMode mode) {
        switch (mode) {
            case PREFIX:
                return Component.translatable("playernotes.gui.label.style_prefix");
            case SUFFIX:
                return Component.translatable("playernotes.gui.label.style_suffix");
            case PLAYER_NAME:
                return Component.translatable("playernotes.gui.label.style_player_name");
            case WHOLE:
                return Component.translatable("playernotes.gui.label.style_whole");
            default:
                return Component.translatable("playernotes.gui.label.style_prefix");
        }
    }

    private Component getStyleTextLabel(StyleMode mode) {
        switch (mode) {
            case PREFIX:
                return Component.translatable("playernotes.gui.label.prefix");
            case SUFFIX:
                return Component.translatable("playernotes.gui.label.suffix");
            case PLAYER_NAME:
                return Component.translatable("playernotes.gui.label.player_name_style");
            case WHOLE:
                return Component.translatable("playernotes.gui.label.whole_style");
            default:
                return Component.translatable("playernotes.gui.label.prefix");
        }
    }

    private void cycleStyleMode() {
        StyleMode[] modes = StyleMode.values();
        int currentIndex = currentStyleMode.ordinal();
        int nextIndex = (currentIndex + 1) % modes.length;
        currentStyleMode = modes[nextIndex];
        styleModeBtn.setMessage(getStyleModeLabel(currentStyleMode));
    }

    @Override
    public void onClose() {
        if (isNewList) {
            NoteListManager.removeNoteList(noteList);
        }
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    private void saveCurrentState() {
        if (nameField != null) {
            currentName = nameField.getValue();
        }
        if (styleTextField != null) {
            currentStyleText = styleTextField.getValue();
        }
        if (enabledCheckbox != null) {
            currentEnabled = enabledCheckbox.selected();
        }
    }

    private void addPlayer() {
        String playerName = addPlayerField.getValue().trim();
        if (!playerName.isEmpty()) {
            PlayerEntry testEntry = new PlayerEntry(playerName, "");
            if (noteList.containsPlayer(testEntry)) {
                showFeedback(Component.translatable("playernotes.gui.message.player_already_in_list").withStyle(ChatFormatting.RED));
                return;
            }
            UUID playerUUID = getPlayerUUID(playerName);
            if (playerUUID != null) {
                noteList.addPlayer(playerName, playerUUID);
            } else {
                noteList.addPlayer(playerName, "");
            }
            addPlayerField.setValue("");
            playersWidget.updateList();
            NoteListManager.save();
            showFeedback(Component.translatable("playernotes.gui.message.player_added").withStyle(ChatFormatting.GREEN));
        }
    }

    private void showFeedback(Component message) {
        if (feedbackWidget != null) {
            feedbackWidget.setMessage(message);
            feedbackTime = System.currentTimeMillis();
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
        String newName = nameField.getValue();
        if (NoteListManager.listNameExistsExcluding(newName, noteList)) {
            showFeedback(Component.translatable("playernotes.gui.message.list_name_exists").withStyle(ChatFormatting.RED));
            return;
        }
        noteList.setName(newName);
        noteList.setStyleMode(currentStyleMode);
        noteList.setStyleText(styleTextField.getValue());
        noteList.setEnabled(enabledCheckbox.selected());

        if (isNewList && !NoteListManager.getNoteLists().contains(noteList)) {
            NoteListManager.addNoteList(noteList);
        }
        NoteListManager.save();

        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        context.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        int rightColumnX = this.width / 2 + 10;
        context.drawString(this.font, Component.translatable("playernotes.gui.label.players").getString() + ":", rightColumnX, 10, 0xAAAAAA);

        StringBuilder preview = new StringBuilder();
        String styleText = styleTextField.getValue().replace("&", "§");
        switch (currentStyleMode) {
            case PREFIX:
                preview.append(styleText).append("§r ").append("PlayerName");
                break;
            case SUFFIX:
                preview.append("PlayerName").append(" ").append(styleText);
                break;
            case PLAYER_NAME:
                preview.append(styleText).append("PlayerName").append("§r");
                break;
            case WHOLE:
                preview.append(styleText).append("PlayerName");
                break;
        }
        context.drawString(this.font, Component.translatable("playernotes.gui.label.preview").getString() + ": " + preview, 10, this.height - 50, 0xFFFFFF);

        if (feedbackWidget != null) {
            if (System.currentTimeMillis() - feedbackTime > 3000) {
                feedbackWidget.setMessage(Component.empty());
            }
        }

        nameField.render(context, mouseX, mouseY, delta);
        styleTextField.render(context, mouseX, mouseY, delta);
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
                {"&0", "Black"}, {"&1", "Dark Blue"}, {"&2", "Dark Green"}, {"&3", "Dark Aqua"},
                {"&4", "Dark Red"}, {"&5", "Dark Purple"}, {"&6", "Gold"}, {"&7", "Gray"},
                {"&8", "Dark Gray"}, {"&9", "Blue"}, {"&a", "Green"}, {"&b", "Aqua"},
                {"&c", "Red"}, {"&d", "Light Purple"}, {"&e", "Yellow"}, {"&f", "White"}
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

            StringWidget wikiWidget = new StringWidget(x, y, 300, 12, 
                Component.translatable("playernotes.gui.help.wiki"), this.font);
            textWidgets.add(wikiWidget);
            this.addRenderableOnly(wikiWidget);
            y += lineHeight + 10;

            StringWidget featuresLabel = new StringWidget(x, y, 100, 12, 
                Component.translatable("playernotes.gui.help.features"), this.font);
            textWidgets.add(featuresLabel);
            this.addRenderableOnly(featuresLabel);
            y += lineHeight;

            StringWidget modePrefixWidget = new StringWidget(x + 10, y, 300, 12, 
                Component.translatable("playernotes.gui.help.mode_prefix"), this.font);
            textWidgets.add(modePrefixWidget);
            this.addRenderableOnly(modePrefixWidget);
            y += lineHeight;

            StringWidget modeSuffixWidget = new StringWidget(x + 10, y, 300, 12, 
                Component.translatable("playernotes.gui.help.mode_suffix"), this.font);
            textWidgets.add(modeSuffixWidget);
            this.addRenderableOnly(modeSuffixWidget);
            y += lineHeight;

            StringWidget modePlayerNameWidget = new StringWidget(x + 10, y, 300, 12, 
                Component.translatable("playernotes.gui.help.mode_player_name"), this.font);
            textWidgets.add(modePlayerNameWidget);
            this.addRenderableOnly(modePlayerNameWidget);
            y += lineHeight;

            StringWidget modeWholeWidget = new StringWidget(x + 10, y, 300, 12, 
                Component.translatable("playernotes.gui.help.mode_whole"), this.font);
            textWidgets.add(modeWholeWidget);
            this.addRenderableOnly(modeWholeWidget);
            y += lineHeight + 20;

            this.addRenderableWidget(Button.builder(
                    Component.translatable("playernotes.gui.button.done"),
                    (button) -> {
                        if (this.minecraft != null) {
                            this.minecraft.setScreen(parent);
                        }
                    })
                    .size(100, 20)
                    .pos(this.width / 2 - 50, this.height - 30)
                    .build());
        }

        @Override
        public void onClose() {
            if (this.minecraft != null) {
                this.minecraft.setScreen(parent);
            }
        }
    }
}
