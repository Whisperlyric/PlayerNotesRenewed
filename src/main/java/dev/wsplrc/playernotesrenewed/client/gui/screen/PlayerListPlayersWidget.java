package dev.wsplrc.playernotesrenewed.client.gui.screen;

import dev.wsplrc.playernotesrenewed.client.objects.PlayerEntry;
import dev.wsplrc.playernotesrenewed.client.objects.NoteList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListPlayersWidget extends ContainerObjectSelectionList<PlayerListPlayersWidget.Entry> {
    private final NoteList noteList;
    private PlayerEntry selectedPlayer;
    private final int entryWidth;

    public PlayerListPlayersWidget(Minecraft minecraft, int width, int height, int y, int itemHeight, NoteList noteList) {
        super(minecraft, width, height, y, itemHeight);
        this.noteList = noteList;
        this.entryWidth = width - 10;
        this.centerListVertically = false;
        updateList();
    }

    @Override
    public int getRowWidth() {
        return entryWidth;
    }

    public void updateList() {
        this.clearEntries();
        for (PlayerEntry player : noteList.getPlayers()) {
            this.addEntry(new PlayerEntryWidget(player, this));
        }
    }

    public void updateOnlineStatus() {
        Minecraft mc = Minecraft.getInstance();
        boolean isConnected = mc.getConnection() != null;
        
        for (PlayerEntry player : noteList.getPlayers()) {
            if (!isConnected) {
                player.setOnlineStatus(PlayerEntry.OnlineStatus.UNDEFINED);
            } else {
                UUID uuid = player.getUUID();
                if (uuid != null) {
                    PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(uuid);
                    if (playerInfo != null) {
                        player.setOnlineStatus(PlayerEntry.OnlineStatus.ONLINE);
                        if (player.getName() == null || player.getName().isEmpty() || "Unknown".equals(player.getName())) {
                            player.setName(playerInfo.getProfile().name());
                        }
                    } else {
                        player.setOnlineStatus(PlayerEntry.OnlineStatus.OFFLINE);
                    }
                } else {
                    String name = player.getName();
                    if (name != null && !name.isEmpty() && !"Unknown".equals(name)) {
                        boolean found = false;
                        for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
                            if (info.getProfile().name().equalsIgnoreCase(name)) {
                                player.setOnlineStatus(PlayerEntry.OnlineStatus.ONLINE);
                                player.setUuid(info.getProfile().id().toString());
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            player.setOnlineStatus(PlayerEntry.OnlineStatus.OFFLINE);
                        }
                    } else {
                        player.setOnlineStatus(PlayerEntry.OnlineStatus.OFFLINE);
                    }
                }
            }
        }
        updateList();
    }

    public void selectPlayer(PlayerEntry player) {
        this.selectedPlayer = player;
    }

    public PlayerEntry getSelectedPlayer() {
        return selectedPlayer;
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

    public static class PlayerEntryWidget extends Entry {
        private final PlayerEntry player;
        private final PlayerListPlayersWidget parent;
        private final StringWidget nameWidget;

        public PlayerEntryWidget(PlayerEntry player, PlayerListPlayersWidget parent) {
            this.player = player;
            this.parent = parent;

            String displayName = player.getName();
            this.nameWidget = new StringWidget(0, 0, 100, 20, Component.literal(displayName), Minecraft.getInstance().font);
            elements.add(nameWidget);
        }

        @Override
        public void renderContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = getContentX();
            int y = getContentY();
            int width = getContentWidth();
            int height = getContentHeight();

            if (hovered || (parent.selectedPlayer != null && parent.selectedPlayer.equals(player))) {
                guiGraphics.fill(x, y, x + width, y + height, 0x80FFFFFF);
            }

            int centerY = y + (height - 10) / 2 - 1;

            nameWidget.setX(x + 5);
            nameWidget.setY(centerY);
            nameWidget.render(guiGraphics, mouseX, mouseY, tickDelta);

            int statusColor;
            switch (player.getOnlineStatus()) {
                case ONLINE:
                    statusColor = 0xFF55FF55;
                    break;
                case OFFLINE:
                    statusColor = 0xFFFF5555;
                    break;
                default:
                    statusColor = 0xFFAAAAAA;
            }
            
            String statusText = player.getStatusDisplayTextPlain();
            guiGraphics.drawString(Minecraft.getInstance().font, statusText, x + width - 85, centerY + 1, statusColor);
        }

        @Override
        public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
            parent.selectPlayer(player);
            return true;
        }
    }
}
