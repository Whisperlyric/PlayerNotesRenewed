package dev.wsplrc.playernotesrenewed.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.wsplrc.playernotesrenewed.client.objects.NoteList;
import dev.wsplrc.playernotesrenewed.client.objects.PlayerEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NoteListManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static List<NoteList> noteLists = new ArrayList<>();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("PlayerNotes");
    private static final String NOTE_LISTS_FILE = "NoteLists.json";

    public static void load() {
        try {
            Files.createDirectories(CONFIG_PATH);
            var file = CONFIG_PATH.resolve(NOTE_LISTS_FILE);
            if (Files.exists(file)) {
                String json = Files.readString(file);
                noteLists = GSON.fromJson(json, new TypeToken<List<NoteList>>() {}.getType());
                if (noteLists == null) {
                    noteLists = new ArrayList<>();
                }
            } else {
                NoteList defaultList = new NoteList("Default");
                defaultList.setPrefix("[N]");
                noteLists.add(defaultList);
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH);
            var file = CONFIG_PATH.resolve(NOTE_LISTS_FILE);
            Files.writeString(file, GSON.toJson(noteLists));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<NoteList> getNoteLists() {
        return new ArrayList<>(noteLists);
    }

    public static List<NoteList> getEnabledNoteLists() {
        return noteLists.stream()
                .filter(NoteList::isEnabled)
                .sorted(Comparator.comparingInt(NoteList::getPriority))
                .collect(Collectors.toList());
    }

    public static void addNoteList(NoteList noteList) {
        noteLists.add(noteList);
        save();
    }

    public static void removeNoteList(NoteList noteList) {
        noteLists.remove(noteList);
        save();
    }

    public static void updateNoteList(NoteList oldList, NoteList newList) {
        int index = noteLists.indexOf(oldList);
        if (index >= 0) {
            noteLists.set(index, newList);
            save();
        }
    }

    public static void moveNoteListUp(NoteList noteList) {
        int index = noteLists.indexOf(noteList);
        if (index > 0) {
            NoteList temp = noteLists.get(index - 1);
            noteLists.set(index - 1, noteList);
            noteLists.set(index, temp);
            updatePriorities();
            save();
        }
    }

    public static void moveNoteListDown(NoteList noteList) {
        int index = noteLists.indexOf(noteList);
        if (index < noteLists.size() - 1) {
            NoteList temp = noteLists.get(index + 1);
            noteLists.set(index + 1, noteList);
            noteLists.set(index, temp);
            updatePriorities();
            save();
        }
    }

    private static void updatePriorities() {
        for (int i = 0; i < noteLists.size(); i++) {
            noteLists.get(i).setPriority(i);
        }
    }

    public static void updateAllOnlineStatus() {
        Minecraft mc = Minecraft.getInstance();
        boolean isConnected = mc.getConnection() != null;
        
        for (NoteList list : noteLists) {
            for (PlayerEntry player : list.getPlayers()) {
                if (!isConnected) {
                    player.setOnlineStatus(PlayerEntry.OnlineStatus.UNDEFINED);
                } else {
                    updatePlayerOnlineStatus(player, mc);
                }
            }
        }
        save();
    }

    private static void updatePlayerOnlineStatus(PlayerEntry player, Minecraft mc) {
        UUID uuid = player.getUUID();
        if (uuid != null) {
            PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(uuid);
            if (playerInfo != null) {
                player.setOnlineStatus(PlayerEntry.OnlineStatus.ONLINE);
                if (player.getName() == null || player.getName().isEmpty() || "Unknown".equals(player.getName())) {
                    player.setName(playerInfo.getProfile().name());
                }
            } else {
                player.setOnlineStatus(PlayerEntry.OnlineStatus.OFFLINE);
            }
        } else {
            String name = player.getName();
            if (name != null && !name.isEmpty() && !"Unknown".equals(name)) {
                boolean found = false;
                for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
                    if (info.getProfile().name().equalsIgnoreCase(name)) {
                        player.setOnlineStatus(PlayerEntry.OnlineStatus.ONLINE);
                        player.setUuid(info.getProfile().id().toString());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    player.setOnlineStatus(PlayerEntry.OnlineStatus.OFFLINE);
                }
            } else {
                player.setOnlineStatus(PlayerEntry.OnlineStatus.OFFLINE);
            }
        }
    }

    public static List<String> getPrefixesForPlayer(String uuid) {
        List<String> prefixes = new ArrayList<>();
        for (NoteList list : getEnabledNoteLists()) {
            if (list.containsPlayerByUUID(uuid)) {
                prefixes.add(list.getFormattedPrefix());
            }
        }
        return prefixes;
    }

    public static List<String> getPrefixesForPlayerByName(String name) {
        List<String> prefixes = new ArrayList<>();
        for (NoteList list : getEnabledNoteLists()) {
            if (list.containsPlayerByName(name)) {
                prefixes.add(list.getFormattedPrefix());
            }
        }
        return prefixes;
    }

    public static boolean playerHasPrefix(String uuid) {
        for (NoteList list : getEnabledNoteLists()) {
            if (list.containsPlayerByUUID(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean playerHasPrefixByName(String name) {
        for (NoteList list : getEnabledNoteLists()) {
            if (list.containsPlayerByName(name)) {
                return true;
            }
        }
        return false;
    }
}
