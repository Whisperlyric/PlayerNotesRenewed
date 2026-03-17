package dev.wsplrc.playernotesrenewed.client.objects;

import java.util.ArrayList;
import java.util.List;

public class LegacyNoteList {
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
    private boolean styleAffectPlayerName;
    private int prefixPriority;
    private int suffixPriority;

    public LegacyNoteList() {
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
        this.styleAffectPlayerName = true;
        this.prefixPriority = 0;
        this.suffixPriority = 0;
    }

    public boolean isLegacyFormat() {
        return true;
    }

    public boolean hasLegacyFields() {
        return prefixAppendReset || suffixAppendReset || !styleAffectPlayerName;
    }

    public String getName() { return name; }
    public String getPrefix() { return prefix != null ? prefix : ""; }
    public String getSuffix() { return suffix != null ? suffix : ""; }
    public List<PlayerEntry> getPlayers() { 
        if (players == null) {
            players = new ArrayList<>();
        }
        return players; 
    }
    public int getPriority() { return priority; }
    public boolean isEnabled() { return enabled; }
    public boolean isPrefixEnabled() { return prefixEnabled; }
    public boolean isSuffixEnabled() { return suffixEnabled; }
    public boolean isPrefixAppendReset() { return prefixAppendReset; }
    public boolean isSuffixAppendReset() { return suffixAppendReset; }
    public boolean isStyleAffectPlayerName() { return styleAffectPlayerName; }
    public int getPrefixPriority() { return prefixPriority; }
    public int getSuffixPriority() { return suffixPriority; }
}
