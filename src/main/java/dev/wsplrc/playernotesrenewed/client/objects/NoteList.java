package dev.wsplrc.playernotesrenewed.client.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteList {
    private String name;
    private StyleMode styleMode;
    private String styleText;
    private String wholePrefix;
    private String wholePlayerNameStyle;
    private String wholeSuffix;
    private boolean overridePlayerName;
    private int priority;
    private boolean enabled;
    private List<PlayerEntry> players;

    public NoteList() {
        this.name = "Unnamed List";
        this.styleMode = StyleMode.PREFIX;
        this.styleText = "[N]";
        this.wholePrefix = "";
        this.wholePlayerNameStyle = "";
        this.wholeSuffix = "";
        this.overridePlayerName = false;
        this.priority = 0;
        this.enabled = true;
        this.players = new ArrayList<>();
    }

    public NoteList(String name) {
        this();
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public StyleMode getStyleMode() { return styleMode != null ? styleMode : StyleMode.PREFIX; }
    public void setStyleMode(StyleMode styleMode) { this.styleMode = styleMode; }

    public String getStyleText() { return styleText != null ? styleText : ""; }
    public void setStyleText(String styleText) { this.styleText = styleText; }

    public String getWholePrefix() { return wholePrefix != null ? wholePrefix : ""; }
    public void setWholePrefix(String wholePrefix) { this.wholePrefix = wholePrefix; }

    public String getWholePlayerNameStyle() { return wholePlayerNameStyle != null ? wholePlayerNameStyle : ""; }
    public void setWholePlayerNameStyle(String wholePlayerNameStyle) { this.wholePlayerNameStyle = wholePlayerNameStyle; }

    public String getWholeSuffix() { return wholeSuffix != null ? wholeSuffix : ""; }
    public void setWholeSuffix(String wholeSuffix) { this.wholeSuffix = wholeSuffix; }

    public boolean isOverridePlayerName() { return overridePlayerName; }
    public void setOverridePlayerName(boolean overridePlayerName) { this.overridePlayerName = overridePlayerName; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public List<PlayerEntry> getPlayers() { 
        if (players == null) {
            players = new ArrayList<>();
        }
        return players; 
    }
    public void setPlayers(List<PlayerEntry> players) { this.players = players; }

    public void addPlayer(PlayerEntry player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        if (!containsPlayer(player)) {
            players.add(player);
        }
    }

    public void addPlayer(String name, String uuid) {
        addPlayer(new PlayerEntry(name, uuid));
    }

    public void addPlayer(String name, UUID uuid) {
        addPlayer(new PlayerEntry(name, uuid));
    }

    public void removePlayer(PlayerEntry player) {
        if (players != null) {
            players.remove(player);
        }
    }

    public void removePlayer(String uuidOrName) {
        if (players != null) {
            players.removeIf(p -> p.getUuid().equals(uuidOrName) || p.getName().equals(uuidOrName));
        }
    }

    public boolean containsPlayer(PlayerEntry player) {
        if (players == null || player == null) return false;
        String uuid = player.getUuid();
        String name = player.getName();
        for (PlayerEntry entry : players) {
            if (uuid != null && !uuid.isEmpty() && uuid.equals(entry.getUuid())) {
                return true;
            }
            if (name != null && !name.isEmpty() && name.equalsIgnoreCase(entry.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPlayerByUUID(String uuid) {
        if (players == null || uuid == null) return false;
        for (PlayerEntry entry : players) {
            if (uuid.equals(entry.getUuid())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPlayerByName(String name) {
        if (players == null || name == null) return false;
        for (PlayerEntry entry : players) {
            if (name.equalsIgnoreCase(entry.getName())) {
                return true;
            }
        }
        return false;
    }

    public PlayerEntry getPlayerByUUID(String uuid) {
        if (players == null || uuid == null) return null;
        for (PlayerEntry entry : players) {
            if (uuid.equals(entry.getUuid())) {
                return entry;
            }
        }
        return null;
    }

    public String getFormattedStyleText() {
        return getStyleText().replace("&", "§");
    }

    public String getFormattedPrefix() {
        String formatted = getStyleText().replace("&", "§");
        if (!formatted.endsWith("§r")) {
            formatted += "§r";
        }
        return formatted;
    }

    public String getFormattedSuffix() {
        return getStyleText().replace("&", "§");
    }

    public String getFormattedPlayerNameStyle() {
        return getStyleText().replace("&", "§");
    }

    public String getFormattedWholePrefix() {
        return getWholePrefix().replace("&", "§");
    }

    public String getFormattedWholePlayerNameStyle() {
        return getWholePlayerNameStyle().replace("&", "§");
    }

    public String getFormattedWholeSuffix() {
        return getWholeSuffix().replace("&", "§");
    }

    public NoteList copy() {
        NoteList copy = new NoteList(this.name + " (Copy)");
        copy.setStyleMode(this.styleMode);
        copy.setStyleText(this.styleText);
        copy.setWholePrefix(this.wholePrefix);
        copy.setWholePlayerNameStyle(this.wholePlayerNameStyle);
        copy.setWholeSuffix(this.wholeSuffix);
        copy.setOverridePlayerName(this.overridePlayerName);
        copy.setEnabled(this.enabled);
        copy.setPriority(this.priority);
        for (PlayerEntry entry : this.getPlayers()) {
            copy.addPlayer(new PlayerEntry(entry.getName(), entry.getUuid()));
        }
        return copy;
    }
}
