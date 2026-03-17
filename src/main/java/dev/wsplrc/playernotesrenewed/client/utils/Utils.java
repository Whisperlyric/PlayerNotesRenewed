package dev.wsplrc.playernotesrenewed.client.utils;

import com.mojang.authlib.GameProfile;
import dev.wsplrc.playernotesrenewed.client.config.NoteListManager;
import dev.wsplrc.playernotesrenewed.client.objects.PrefixEntry;

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

    public static List<PrefixEntry> getPlayerPrefixEntries(String uuid) {
        String normalizedUuid = normalizeUuid(uuid);
        return NoteListManager.getPrefixEntriesForPlayer(normalizedUuid);
    }

    public static List<PrefixEntry> getPlayerPrefixEntriesByProfile(GameProfile profile) {
        String uuid = profile.id().toString();
        List<PrefixEntry> entries = NoteListManager.getPrefixEntriesForPlayer(uuid);
        if (entries.isEmpty()) {
            entries = NoteListManager.getPrefixEntriesForPlayerByName(profile.name());
        }
        return entries;
    }

    public static List<PrefixEntry> getPlayerSuffixEntries(String uuid) {
        String normalizedUuid = normalizeUuid(uuid);
        return NoteListManager.getSuffixEntriesForPlayer(normalizedUuid);
    }

    public static List<PrefixEntry> getPlayerSuffixEntriesByProfile(GameProfile profile) {
        String uuid = profile.id().toString();
        List<PrefixEntry> entries = NoteListManager.getSuffixEntriesForPlayer(uuid);
        if (entries.isEmpty()) {
            entries = NoteListManager.getSuffixEntriesForPlayerByName(profile.name());
        }
        return entries;
    }

    public static String getPlayerPrefixes(String uuid) {
        String normalizedUuid = normalizeUuid(uuid);
        List<String> prefixes = NoteListManager.getPrefixesForPlayer(normalizedUuid);
        return buildPrefixString(prefixes);
    }

    public static String getPlayerPrefixesByProfile(GameProfile profile) {
        String uuid = profile.id().toString();
        List<String> prefixes = NoteListManager.getPrefixesForPlayer(uuid);
        if (prefixes.isEmpty()) {
            prefixes = NoteListManager.getPrefixesForPlayerByName(profile.name());
        }
        return buildPrefixString(prefixes);
    }

    public static String getPlayerSuffixes(String uuid) {
        String normalizedUuid = normalizeUuid(uuid);
        List<String> suffixes = NoteListManager.getSuffixesForPlayer(normalizedUuid);
        return buildSuffixString(suffixes);
    }

    public static String getPlayerSuffixesByProfile(GameProfile profile) {
        String uuid = profile.id().toString();
        List<String> suffixes = NoteListManager.getSuffixesForPlayer(uuid);
        if (suffixes.isEmpty()) {
            suffixes = NoteListManager.getSuffixesForPlayerByName(profile.name());
        }
        return buildSuffixString(suffixes);
    }

    private static String buildPrefixString(List<String> prefixes) {
        if (prefixes == null || prefixes.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (String prefix : prefixes) {
            result.append(prefix).append(" ");
        }
        return result.toString();
    }

    private static String buildSuffixString(List<String> suffixes) {
        if (suffixes == null || suffixes.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (String suffix : suffixes) {
            result.append(" ").append(suffix);
        }
        return result.toString();
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
