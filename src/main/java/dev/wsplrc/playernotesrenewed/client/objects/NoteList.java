package dev.wsplrc.playernotesrenewed.client.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteList {
    private String name;
    private String prefix;
    private String suffix;
    private String playerNamePrefix;
    private String playerNameSuffix;
    private List<PlayerEntry> players;
    private int priority;
    private boolean enabled;
    private boolean prefixEnabled;
    private boolean suffixEnabled;
    private int prefixPriority;
    private int suffixPriority;
    private boolean prefixStyleEnabled;
    private boolean suffixStyleEnabled;
    private boolean playerNameStyleEnabled;
    private boolean wholeStyleEnabled;

    public NoteList() {
        this.name = "Unnamed List";
        this.prefix = "[N]";
        this.suffix = "";
        this.playerNamePrefix = "";
        this.playerNameSuffix = "";
        this.players = new ArrayList<>();
        this.priority = 0;
        this.enabled = true;
        this.prefixEnabled = true;
        this.suffixEnabled = false;
        this.prefixPriority = 0;
        this.suffixPriority = 0;
        this.prefixStyleEnabled = false;
        this.suffixStyleEnabled = false;
        this.playerNameStyleEnabled = false;
        this.wholeStyleEnabled = false;
    }

    public NoteList(String name) {
        this.name = name;
        this.prefix = "[N]";
        this.suffix = "";
        this.playerNamePrefix = "";
        this.playerNameSuffix = "";
        this.players = new ArrayList<>();
        this.priority = 0;
        this.enabled = true;
        this.prefixEnabled = true;
        this.suffixEnabled = false;
        this.prefixPriority = 0;
        this.suffixPriority = 0;
        this.prefixStyleEnabled = false;
        this.suffixStyleEnabled = false;
        this.playerNameStyleEnabled = false;
        this.wholeStyleEnabled = false;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public String getSuffix() { return suffix != null ? suffix : ""; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    public String getPlayerNamePrefix() { return playerNamePrefix != null ? playerNamePrefix : ""; }
    public void setPlayerNamePrefix(String playerNamePrefix) { this.playerNamePrefix = playerNamePrefix; }

    public String getPlayerNameSuffix() { return playerNameSuffix != null ? playerNameSuffix : ""; }
    public void setPlayerNameSuffix(String playerNameSuffix) { this.playerNameSuffix = playerNameSuffix; }

    public boolean isPrefixEnabled() { return prefixEnabled; }
    public void setPrefixEnabled(boolean prefixEnabled) { this.prefixEnabled = prefixEnabled; }

    public boolean isSuffixEnabled() { return suffixEnabled; }
    public void setSuffixEnabled(boolean suffixEnabled) { this.suffixEnabled = suffixEnabled; }

    public int getPrefixPriority() { return prefixPriority; }
    public void setPrefixPriority(int prefixPriority) { this.prefixPriority = prefixPriority; }

    public int getSuffixPriority() { return suffixPriority; }
    public void setSuffixPriority(int suffixPriority) { this.suffixPriority = suffixPriority; }

    public boolean isPrefixStyleEnabled() { return prefixStyleEnabled; }
    public void setPrefixStyleEnabled(boolean prefixStyleEnabled) { this.prefixStyleEnabled = prefixStyleEnabled; }

    public boolean isSuffixStyleEnabled() { return suffixStyleEnabled; }
    public void setSuffixStyleEnabled(boolean suffixStyleEnabled) { this.suffixStyleEnabled = suffixStyleEnabled; }

    public boolean isPlayerNameStyleEnabled() { return playerNameStyleEnabled; }
    public void setPlayerNameStyleEnabled(boolean playerNameStyleEnabled) { this.playerNameStyleEnabled = playerNameStyleEnabled; }

    public boolean isWholeStyleEnabled() { return wholeStyleEnabled; }
    public void setWholeStyleEnabled(boolean wholeStyleEnabled) { this.wholeStyleEnabled = wholeStyleEnabled; }

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

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getFormattedPrefix() {
        String formatted = prefix.replace("&", "§");
        formatted += "§r";
        return formatted;
    }

    public String getFormattedSuffix() {
        String formatted = getSuffix().replace("&", "§");
        return formatted;
    }

    public String getFormattedPlayerNamePrefix() {
        return getPlayerNamePrefix().replace("&", "§");
    }

    public String getFormattedPlayerNameSuffix() {
        return getPlayerNameSuffix().replace("&", "§");
    }

    public NoteList copy() {
        NoteList copy = new NoteList(this.name + " (Copy)");
        copy.setPrefix(this.prefix);
        copy.setSuffix(this.suffix);
        copy.setPlayerNamePrefix(this.playerNamePrefix);
        copy.setPlayerNameSuffix(this.playerNameSuffix);
        copy.setEnabled(this.enabled);
        copy.setPrefixEnabled(this.prefixEnabled);
        copy.setSuffixEnabled(this.suffixEnabled);
        copy.setPrefixPriority(this.prefixPriority);
        copy.setSuffixPriority(this.suffixPriority);
        copy.setPrefixStyleEnabled(this.prefixStyleEnabled);
        copy.setSuffixStyleEnabled(this.suffixStyleEnabled);
        copy.setPlayerNameStyleEnabled(this.playerNameStyleEnabled);
        copy.setWholeStyleEnabled(this.wholeStyleEnabled);
        for (PlayerEntry entry : this.getPlayers()) {
            copy.addPlayer(new PlayerEntry(entry.getName(), entry.getUuid()));
        }
        return copy;
    }
}
