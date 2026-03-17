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
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListEditScreen extends Screen {
    private final Screen parent;
    private final NoteList noteList;
    private final boolean isNewList;
    private String currentName;
    private String currentPrefix;
    private String currentSuffix;
    private boolean currentEnabled;
    private boolean currentPrefixEnabled;
    private boolean currentSuffixEnabled;
    private boolean currentPrefixAppendReset;
    private boolean currentSuffixAppendReset;
    private boolean currentStyleAffectPlayerName;
    private int currentPrefixPriority;
    private int currentSuffixPriority;
    private EditBox nameField;
    private EditBox prefixField;
    private EditBox suffixField;
    private EditBox prefixPriorityField;
    private EditBox suffixPriorityField;
    private Checkbox enabledCheckbox;
    private Checkbox prefixEnabledCheckbox;
    private Checkbox suffixEnabledCheckbox;
    private Checkbox prefixResetCheckbox;
    private Checkbox suffixResetCheckbox;
    private Checkbox styleAffectPlayerNameCheckbox;
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
        this.currentPrefix = noteList.getPrefix();
        this.currentSuffix = noteList.getSuffix();
        this.currentEnabled = noteList.isEnabled();
        this.currentPrefixEnabled = noteList.isPrefixEnabled();
        this.currentSuffixEnabled = noteList.isSuffixEnabled();
        this.currentPrefixAppendReset = noteList.isPrefixAppendReset();
        this.currentSuffixAppendReset = noteList.isSuffixAppendReset();
        this.currentStyleAffectPlayerName = noteList.isStyleAffectPlayerName();
        this.currentPrefixPriority = noteList.getPrefixPriority();
        this.currentSuffixPriority = noteList.getSuffixPriority();
    }

    @Override
    protected void init() {
        super.init();

        int leftColumnX = 10;
        int leftColumnWidth = Math.min(this.width / 2 - 30, 200);
        int rightColumnX = this.width / 2 + 10;
        int rightColumnWidth = this.width / 2 - 30;

        int yPos = 20;

        nameField = new EditBox(this.font, leftColumnX, yPos, leftColumnWidth, 18, Component.translatable("playernotes.gui.label.list_name"));
        nameField.setValue(currentName);
        this.addRenderableWidget(nameField);
        yPos += 22;

        prefixField = new EditBox(this.font, leftColumnX, yPos, leftColumnWidth - 22, 18, Component.translatable("playernotes.gui.label.prefix"));
        prefixField.setValue(currentPrefix);
        this.addRenderableWidget(prefixField);

        prefixEnabledCheckbox = Checkbox.builder(Component.empty(), this.font)
                .pos(leftColumnX + leftColumnWidth - 20, yPos)
                .selected(currentPrefixEnabled)
                .build();
        this.addRenderableWidget(prefixEnabledCheckbox);
        yPos += 22;

        suffixField = new EditBox(this.font, leftColumnX, yPos, leftColumnWidth - 22, 18, Component.translatable("playernotes.gui.label.suffix"));
        suffixField.setValue(currentSuffix);
        this.addRenderableWidget(suffixField);

        suffixEnabledCheckbox = Checkbox.builder(Component.empty(), this.font)
                .pos(leftColumnX + leftColumnWidth - 20, yPos)
                .selected(currentSuffixEnabled)
                .build();
        this.addRenderableWidget(suffixEnabledCheckbox);
        yPos += 22;

        prefixResetCheckbox = Checkbox.builder(Component.translatable("playernotes.gui.label.prefix_reset"), this.font)
                .pos(leftColumnX, yPos)
                .selected(currentPrefixAppendReset)
                .build();
        this.addRenderableWidget(prefixResetCheckbox);
        yPos += 20;

        suffixResetCheckbox = Checkbox.builder(Component.translatable("playernotes.gui.label.suffix_reset"), this.font)
                .pos(leftColumnX, yPos)
                .selected(currentSuffixAppendReset)
                .build();
        this.addRenderableWidget(suffixResetCheckbox);
        yPos += 20;

        styleAffectPlayerNameCheckbox = Checkbox.builder(Component.translatable("playernotes.gui.label.style_affect_name"), this.font)
                .pos(leftColumnX, yPos)
                .selected(currentStyleAffectPlayerName)
                .build();
        this.addRenderableWidget(styleAffectPlayerNameCheckbox);
        yPos += 22;

        StringWidget priorityHint = new StringWidget(leftColumnX, yPos, leftColumnWidth, 12, 
            Component.translatable("playernotes.gui.label.priority_hint"), this.font);
        this.addRenderableOnly(priorityHint);
        yPos += 14;

        prefixPriorityField = new EditBox(this.font, leftColumnX, yPos, 40, 18, Component.translatable("playernotes.gui.label.prefix_priority"));
        prefixPriorityField.setValue(String.valueOf(currentPrefixPriority));
        prefixPriorityField.setFilter(s -> s.matches("-?\\d*"));
        this.addRenderableWidget(prefixPriorityField);

        suffixPriorityField = new EditBox(this.font, leftColumnX + 80, yPos, 40, 18, Component.translatable("playernotes.gui.label.suffix_priority"));
        suffixPriorityField.setValue(String.valueOf(currentSuffixPriority));
        suffixPriorityField.setFilter(s -> s.matches("-?\\d*"));
        this.addRenderableWidget(suffixPriorityField);
        yPos += 22;

        enabledCheckbox = Checkbox.builder(Component.translatable("playernotes.gui.label.enabled"), this.font)
                .pos(leftColumnX, yPos)
                .selected(currentEnabled)
                .build();
        this.addRenderableWidget(enabledCheckbox);
        yPos += 22;

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
        if (prefixField != null) {
            currentPrefix = prefixField.getValue();
        }
        if (suffixField != null) {
            currentSuffix = suffixField.getValue();
        }
        if (enabledCheckbox != null) {
            currentEnabled = enabledCheckbox.selected();
        }
        if (prefixEnabledCheckbox != null) {
            currentPrefixEnabled = prefixEnabledCheckbox.selected();
        }
        if (suffixEnabledCheckbox != null) {
            currentSuffixEnabled = suffixEnabledCheckbox.selected();
        }
        if (prefixResetCheckbox != null) {
            currentPrefixAppendReset = prefixResetCheckbox.selected();
        }
        if (suffixResetCheckbox != null) {
            currentSuffixAppendReset = suffixResetCheckbox.selected();
        }
        if (styleAffectPlayerNameCheckbox != null) {
            currentStyleAffectPlayerName = styleAffectPlayerNameCheckbox.selected();
        }
        if (prefixPriorityField != null) {
            try {
                currentPrefixPriority = Integer.parseInt(prefixPriorityField.getValue());
            } catch (NumberFormatException e) {
                currentPrefixPriority = 0;
            }
        }
        if (suffixPriorityField != null) {
            try {
                currentSuffixPriority = Integer.parseInt(suffixPriorityField.getValue());
            } catch (NumberFormatException e) {
                currentSuffixPriority = 0;
            }
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
        noteList.setPrefix(prefixField.getValue());
        noteList.setSuffix(suffixField.getValue());
        noteList.setEnabled(enabledCheckbox.selected());
        noteList.setPrefixEnabled(prefixEnabledCheckbox.selected());
        noteList.setSuffixEnabled(suffixEnabledCheckbox.selected());
        noteList.setPrefixAppendReset(prefixResetCheckbox.selected());
        noteList.setSuffixAppendReset(suffixResetCheckbox.selected());
        noteList.setStyleAffectPlayerName(styleAffectPlayerNameCheckbox.selected());
        try {
            noteList.setPrefixPriority(Integer.parseInt(prefixPriorityField.getValue()));
        } catch (NumberFormatException e) {
            noteList.setPrefixPriority(0);
        }
        try {
            noteList.setSuffixPriority(Integer.parseInt(suffixPriorityField.getValue()));
        } catch (NumberFormatException e) {
            noteList.setSuffixPriority(0);
        }

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
        if (prefixEnabledCheckbox.selected()) {
            preview.append(noteList.getFormattedPrefix());
        }
        preview.append("PlayerName");
        if (suffixEnabledCheckbox.selected()) {
            preview.append(" ").append(noteList.getFormattedSuffix());
        }
        context.drawString(this.font, Component.translatable("playernotes.gui.label.preview").getString() + ": " + preview, 10, this.height - 50, 0xFFFFFF);

        if (feedbackWidget != null) {
            if (System.currentTimeMillis() - feedbackTime > 3000) {
                feedbackWidget.setMessage(Component.empty());
            }
        }

        nameField.render(context, mouseX, mouseY, delta);
        prefixField.render(context, mouseX, mouseY, delta);
        suffixField.render(context, mouseX, mouseY, delta);
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
            y += lineHeight + 10;

            StringWidget featuresLabel = new StringWidget(x, y, 200, 12, 
                Component.translatable("playernotes.gui.help.features"), this.font);
            textWidgets.add(featuresLabel);
            this.addRenderableOnly(featuresLabel);
            y += lineHeight;

            StringWidget suffixWidget = new StringWidget(x + 10, y, 300, 12, 
                Component.translatable("playernotes.gui.help.suffix"), this.font);
            textWidgets.add(suffixWidget);
            this.addRenderableOnly(suffixWidget);
            y += lineHeight;

            StringWidget priorityWidget = new StringWidget(x + 10, y, 300, 12, 
                Component.translatable("playernotes.gui.help.priority"), this.font);
            textWidgets.add(priorityWidget);
            this.addRenderableOnly(priorityWidget);
            y += lineHeight;

            StringWidget resetWidget = new StringWidget(x + 10, y, 300, 12, 
                Component.translatable("playernotes.gui.help.reset_code"), this.font);
            textWidgets.add(resetWidget);
            this.addRenderableOnly(resetWidget);

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
