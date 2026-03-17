package dev.wsplrc.playernotesrenewed.client.objects;

public class StyleEntry {
    private final String text;
    private final StyleType type;
    private final int priority;

    public enum StyleType {
        PREFIX,
        SUFFIX,
        PLAYER_NAME_PREFIX,
        PLAYER_NAME_SUFFIX,
        WHOLE_STYLE
    }

    public StyleEntry(String text, StyleType type, int priority) {
        this.text = text;
        this.type = type;
        this.priority = priority;
    }

    public String getText() {
        return text;
    }

    public StyleType getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }
}
