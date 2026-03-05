package dev.wsplrc.playernotesrenewed.client.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteList {
    private String name;
    private String prefix;
    private List<PlayerEntry> players;
    private int priority;
    private boolean enabled;

    public NoteList() {
        this.name = "Unnamed List";
        this.prefix = "[N]";
        this.players = new ArrayList<>();
        this.priority = 0;
        this.enabled = true;
    }

    public NoteList(String name) {
        this.name = name;
        this.prefix = "[N]";
        this.players = new ArrayList<>();
        this.priority = 0;
        this.enabled = true;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

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
        return players != null && players.contains(player);
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
        String result = prefix.replace("&", "§");
        result = convertRgbColors(result);
        return result;
    }

    private String convertRgbColors(String text) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (i + 7 <= text.length() && text.charAt(i) == '§' && text.charAt(i + 1) == '#') {
                String hex = text.substring(i + 2, i + 8);
                if (isValidHexColor(hex)) {
                    result.append("§x");
                    for (char c : hex.toCharArray()) {
                        result.append("§").append(c);
                    }
                    i += 8;
                    continue;
                }
            }
            result.append(text.charAt(i));
            i++;
        }
        return result.toString();
    }

    private boolean isValidHexColor(String hex) {
        if (hex.length() != 6) return false;
        for (char c : hex.toCharArray()) {
            if (!Character.isDigit(c) && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
                return false;
            }
        }
        return true;
    }
}
