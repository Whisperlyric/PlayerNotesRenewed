package dev.wsplrc.playernotesrenewed.client.utils;

import com.mojang.authlib.GameProfile;
import dev.wsplrc.playernotesrenewed.client.config.NoteListManager;
import dev.wsplrc.playernotesrenewed.client.objects.StyleEntry;

import java.util.List;

public class Utils {

    public static boolean playerHasPrefix(String uuid) {
        String normalizedUuid = normalizeUuid(uuid);
        return NoteListManager.playerHasPrefix(normalizedUuid);
    }

    public static boolean playerHasPrefixByProfile(GameProfile profile) {
        String uuid = profile.id().toString();
        if (NoteListManager.playerHasPrefix(uuid)) {
            return true;
        }
        return NoteListManager.playerHasPrefixByName(profile.name());
    }

    public static List<StyleEntry> getStyleEntries(String uuid) {
        String normalizedUuid = normalizeUuid(uuid);
        return NoteListManager.getStyleEntriesForPlayer(normalizedUuid);
    }

    public static List<StyleEntry> getStyleEntriesByProfile(GameProfile profile) {
        String uuid = profile.id().toString();
        List<StyleEntry> entries = NoteListManager.getStyleEntriesForPlayer(uuid);
        if (entries.isEmpty()) {
            entries = NoteListManager.getStyleEntriesForPlayerByName(profile.name());
        }
        return entries;
    }

    public static boolean isNotePlayerInLists(GameProfile profile) {
        String uuid = profile.id().toString();
        if (NoteListManager.playerHasPrefix(uuid)) {
            return true;
        }
        return NoteListManager.playerHasPrefixByName(profile.name());
    }

    private static String normalizeUuid(String uuid) {
        if (uuid == null) return "";
        if (uuid.contains("-")) {
            return uuid;
        }
        if (uuid.length() == 32) {
            return uuid.substring(0, 8) + "-" +
                   uuid.substring(8, 12) + "-" +
                   uuid.substring(12, 16) + "-" +
                   uuid.substring(16, 20) + "-" +
                   uuid.substring(20);
        }
        return uuid;
    }
}
