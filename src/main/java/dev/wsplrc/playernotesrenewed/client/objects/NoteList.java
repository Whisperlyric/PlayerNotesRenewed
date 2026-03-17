package dev.wsplrc.playernotesrenewed.client.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteList {
    private String name;
    private String prefix;
    private String suffix;
    private List<PlayerEntry> players;
    private int priority;
    private boolean enabled;
    private boolean prefixEnabled;
    private boolean suffixEnabled;
    private boolean prefixAppendReset;
    private boolean suffixAppendReset;
    private int prefixPriority;
    private int suffixPriority;
    private boolean styleAffectPlayerName;

    public NoteList() {
        this.name = "Unnamed List";
        this.prefix = "[N]";
        this.suffix = "";
        this.players = new ArrayList<>();
        this.priority = 0;
        this.enabled = true;
        this.prefixEnabled = true;
        this.suffixEnabled = false;
        this.prefixAppendReset = true;
        this.suffixAppendReset = true;
        this.prefixPriority = 0;
        this.suffixPriority = 0;
        this.styleAffectPlayerName = false;
    }

    public NoteList(String name) {
        this.name = name;
        this.prefix = "[N]";
        this.suffix = "";
        this.players = new ArrayList<>();
        this.priority = 0;
        this.enabled = true;
        this.prefixEnabled = true;
        this.suffixEnabled = false;
        this.prefixAppendReset = true;
        this.suffixAppendReset = true;
        this.prefixPriority = 0;
        this.suffixPriority = 0;
        this.styleAffectPlayerName = false;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public String getSuffix() { return suffix != null ? suffix : ""; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    public boolean isPrefixEnabled() { return prefixEnabled; }
    public void setPrefixEnabled(boolean prefixEnabled) { this.prefixEnabled = prefixEnabled; }

    public boolean isSuffixEnabled() { return suffixEnabled; }
    public void setSuffixEnabled(boolean suffixEnabled) { this.suffixEnabled = suffixEnabled; }

    public boolean isPrefixAppendReset() { return prefixAppendReset; }
    public void setPrefixAppendReset(boolean prefixAppendReset) { this.prefixAppendReset = prefixAppendReset; }

    public boolean isSuffixAppendReset() { return suffixAppendReset; }
    public void setSuffixAppendReset(boolean suffixAppendReset) { this.suffixAppendReset = suffixAppendReset; }

    public int getPrefixPriority() { return prefixPriority; }
    public void setPrefixPriority(int prefixPriority) { this.prefixPriority = prefixPriority; }

    public int getSuffixPriority() { return suffixPriority; }
    public void setSuffixPriority(int suffixPriority) { this.suffixPriority = suffixPriority; }

    public boolean isStyleAffectPlayerName() { return styleAffectPlayerName; }
    public void setStyleAffectPlayerName(boolean styleAffectPlayerName) { this.styleAffectPlayerName = styleAffectPlayerName; }

    public List<PlayerEntry> getPlayers() { 
        if (players == null) {
            players = new ArrayList<>();
        }
        return players; 
    }
    public void setPlayers(List<PlayerEntry> players) { this.players = players; }

    @Deprecated
    public List<String> getPlayerUUIDs() { 
        List<String> uuids = new ArrayList<>();
        for (PlayerEntry entry : getPlayers()) {
            uuids.add(entry.getUuid());
        }
        return uuids;
    }
    @Deprecated
    public void setPlayerUUIDs(List<String> playerUUIDs) { 
        this.players = new ArrayList<>();
        if (playerUUIDs != null) {
            for (String uuid : playerUUIDs) {
                this.players.add(new PlayerEntry("Unknown", uuid));
            }
        }
    }

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
        if (prefixAppendReset) {
            formatted += "§r";
        }
        return formatted;
    }

    public String getFormattedSuffix() {
        String formatted = getSuffix().replace("&", "§");
        if (suffixAppendReset) {
            formatted += "§r";
        }
        return formatted;
    }

    public NoteList copy() {
        NoteList copy = new NoteList(this.name + " (Copy)");
        copy.setPrefix(this.prefix);
        copy.setSuffix(this.suffix);
        copy.setEnabled(this.enabled);
        copy.setPrefixEnabled(this.prefixEnabled);
        copy.setSuffixEnabled(this.suffixEnabled);
        copy.setPrefixAppendReset(this.prefixAppendReset);
        copy.setSuffixAppendReset(this.suffixAppendReset);
        copy.setPrefixPriority(this.prefixPriority);
        copy.setSuffixPriority(this.suffixPriority);
        copy.setStyleAffectPlayerName(this.styleAffectPlayerName);
        for (PlayerEntry entry : this.getPlayers()) {
            copy.addPlayer(new PlayerEntry(entry.getName(), entry.getUuid()));
        }
        return copy;
    }
}
