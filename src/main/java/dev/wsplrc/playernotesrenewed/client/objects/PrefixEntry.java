package dev.wsplrc.playernotesrenewed.client.objects;

public class PrefixEntry {
    private final String text;
    private final boolean styleAffectPlayerName;

    public PrefixEntry(String text, boolean styleAffectPlayerName) {
        this.text = text;
        this.styleAffectPlayerName = styleAffectPlayerName;
    }

    public String getText() {
        return text;
    }

    public boolean isStyleAffectPlayerName() {
        return styleAffectPlayerName;
    }
}
