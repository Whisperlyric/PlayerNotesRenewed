package dev.wsplrc.playernotesrenewed.client.objects;

public class StyleEntry {
    private final String text;
    private final StyleMode mode;
    private final int priority;

    public StyleEntry(String text, StyleMode mode, int priority) {
        this.text = text;
        this.mode = mode;
        this.priority = priority;
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
}
