package dev.wsplrc.playernotesrenewed.client.objects;

import java.util.UUID;

public class PlayerEntry {
    private String name;
    private String uuid;
    private transient OnlineStatus onlineStatus;

    public enum OnlineStatus {
        UNDEFINED,
        ONLINE,
        OFFLINE
    }

    public PlayerEntry() {
        this.name = "Unknown";
        this.uuid = "";
        this.onlineStatus = OnlineStatus.UNDEFINED;
    }

    public PlayerEntry(String name, String uuid) {
        this.name = name != null ? name : "Unknown";
        this.uuid = uuid != null ? uuid : "";
        this.onlineStatus = OnlineStatus.UNDEFINED;
    }

    public PlayerEntry(String name, UUID uuid) {
        this.name = name != null ? name : "Unknown";
        this.uuid = uuid != null ? uuid.toString() : "";
        this.onlineStatus = OnlineStatus.UNDEFINED;
    }

    public String getName() { 
        return name != null ? name : "Unknown"; 
    }
    public void setName(String name) { this.name = name; }

    public String getUuid() { 
        return uuid != null ? uuid : ""; 
    }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public UUID getUUID() {
        try {
            return uuid != null && !uuid.isEmpty() ? UUID.fromString(uuid) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public OnlineStatus getOnlineStatus() { 
        return onlineStatus != null ? onlineStatus : OnlineStatus.UNDEFINED; 
    }
    public void setOnlineStatus(OnlineStatus onlineStatus) { 
        this.onlineStatus = onlineStatus; 
    }

    public String getStatusDisplayText() {
        switch (getOnlineStatus()) {
            case ONLINE:
                return "§a[在线]§r";
            case OFFLINE:
                return "§c[离线]§r";
            default:
                return "§7[Undefined]§r";
        }
    }

    public String getStatusDisplayTextPlain() {
        switch (getOnlineStatus()) {
            case ONLINE:
                return "[在线]";
            case OFFLINE:
                return "[离线]";
            default:
                return "[Undefined]";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerEntry that = (PlayerEntry) obj;
        if (uuid != null && !uuid.isEmpty() && that.uuid != null && !that.uuid.isEmpty()) {
            return uuid.equals(that.uuid);
        }
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return uuid != null && !uuid.isEmpty() ? uuid.hashCode() : (name != null ? name.hashCode() : 0);
    }
}
