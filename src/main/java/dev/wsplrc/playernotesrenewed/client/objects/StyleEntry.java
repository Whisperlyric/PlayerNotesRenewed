package dev.wsplrc.playernotesrenewed.client.objects;

public class StyleEntry {
    private final String text;
    private final StyleMode mode;
    private final int priority;
    private final String wholePrefix;
    private final String wholePlayerNameStyle;
    private final String wholeSuffix;
    private final boolean overridePlayerName;

    public StyleEntry(String text, StyleMode mode, int priority) {
        this.text = text;
        this.mode = mode;
        this.priority = priority;
        this.wholePrefix = "";
        this.wholePlayerNameStyle = "";
        this.wholeSuffix = "";
        this.overridePlayerName = false;
    }

    public StyleEntry(String wholePrefix, String wholePlayerNameStyle, String wholeSuffix, 
                      boolean overridePlayerName, int priority) {
        this.text = "";
        this.mode = StyleMode.WHOLE;
        this.priority = priority;
        this.wholePrefix = wholePrefix != null ? wholePrefix : "";
        this.wholePlayerNameStyle = wholePlayerNameStyle != null ? wholePlayerNameStyle : "";
        this.wholeSuffix = wholeSuffix != null ? wholeSuffix : "";
        this.overridePlayerName = overridePlayerName;
    }

    public String getText() {
        return text;
    }

    public StyleMode getMode() {
        return mode;
    }

    public int getPriority() {
        return priority;
    }

    public String getWholePrefix() {
        return wholePrefix;
    }

    public String getWholePlayerNameStyle() {
        return wholePlayerNameStyle;
    }

    public String getWholeSuffix() {
        return wholeSuffix;
    }

    public boolean isOverridePlayerName() {
        return overridePlayerName;
    }
}
