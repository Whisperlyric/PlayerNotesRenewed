package dev.wsplrc.playernotesrenewed.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.wsplrc.playernotesrenewed.client.objects.LegacyNoteList;
import dev.wsplrc.playernotesrenewed.client.objects.NoteList;
import dev.wsplrc.playernotesrenewed.client.objects.PlayerEntry;
import dev.wsplrc.playernotesrenewed.client.objects.StyleEntry;
import dev.wsplrc.playernotesrenewed.client.objects.StyleMode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NoteListManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("PlayerNotesRenewed");
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
                
                List<LegacyNoteList> legacyLists = tryLoadLegacyFormat(json);
                if (legacyLists != null) {
                    LOGGER.info("检测到旧版本配置文件，正在进行迁移...");
                    noteLists = migrateFromLegacy(legacyLists);
                    LOGGER.info("配置迁移完成！共迁移 {} 个列表", noteLists.size());
                    LOGGER.warn("==========================================");
                    LOGGER.warn("重要提醒：v1.1.0 已重构样式系统");
                    LOGGER.warn("- 原有配置已自动迁移到新格式");
                    LOGGER.warn("- 样式模式：前缀/后缀/玩家名/整体");
                    LOGGER.warn("- 玩家名和整体模式会覆盖队伍颜色");
                    LOGGER.warn("==========================================");
                    save();
                    return;
                }
                
                noteLists = GSON.fromJson(json, new TypeToken<List<NoteList>>() {}.getType());
                if (noteLists == null) {
                    noteLists = new ArrayList<>();
                }
            } else {
                NoteList defaultList = new NoteList("Default");
                defaultList.setStyleText("[N]");
                noteLists.add(defaultList);
                save();
            }
        } catch (IOException e) {
            LOGGER.error("加载配置文件失败", e);
        }
    }

    private static List<LegacyNoteList> tryLoadLegacyFormat(String json) {
        try {
            List<LegacyNoteList> legacyLists = GSON.fromJson(json, new TypeToken<List<LegacyNoteList>>() {}.getType());
            if (legacyLists != null && !legacyLists.isEmpty()) {
                LegacyNoteList first = legacyLists.get(0);
                if (first.hasLegacyFields()) {
                    return legacyLists;
                }
            }
        } catch (Exception e) {
            LOGGER.debug("非旧版本配置格式");
        }
        return null;
    }

    private static List<NoteList> migrateFromLegacy(List<LegacyNoteList> legacyLists) {
        List<NoteList> migratedLists = new ArrayList<>();
        
        for (LegacyNoteList legacy : legacyLists) {
            NoteList newList = new NoteList();
            newList.setName(legacy.getName());
            newList.setEnabled(legacy.isEnabled());
            newList.setPriority(legacy.getPriority());
            
            boolean hasPrefix = legacy.isPrefixEnabled() && !legacy.getPrefix().isEmpty();
            boolean hasSuffix = legacy.isSuffixEnabled() && !legacy.getSuffix().isEmpty();
            
            if (hasPrefix && hasSuffix) {
                newList.setStyleMode(StyleMode.WHOLE);
                newList.setStyleText(legacy.getPrefix() + " " + legacy.getSuffix());
            } else if (hasPrefix) {
                newList.setStyleMode(StyleMode.PREFIX);
                newList.setStyleText(legacy.getPrefix());
            } else if (hasSuffix) {
                newList.setStyleMode(StyleMode.SUFFIX);
                newList.setStyleText(legacy.getSuffix());
            } else {
                newList.setStyleMode(StyleMode.PREFIX);
                newList.setStyleText("");
            }
            
            for (PlayerEntry player : legacy.getPlayers()) {
                newList.addPlayer(new PlayerEntry(player.getName(), player.getUuid()));
            }
            
            migratedLists.add(newList);
            LOGGER.debug("已迁移列表: {}", legacy.getName());
        }
        
        return migratedLists;
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH);
            var file = CONFIG_PATH.resolve(NOTE_LISTS_FILE);
            Files.writeString(file, GSON.toJson(noteLists));
        } catch (IOException e) {
            LOGGER.error("保存配置文件失败", e);
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
        boolean changed = false;
        
        for (NoteList list : noteLists) {
            for (PlayerEntry player : list.getPlayers()) {
                if (!isConnected) {
                    if (player.setOnlineStatus(PlayerEntry.OnlineStatus.UNDEFINED)) {
                        changed = true;
                    }
                } else {
                    if (updatePlayerOnlineStatus(player, mc)) {
                        changed = true;
                    }
                }
            }
        }
        if (changed) {
            save();
        }
    }

    private static boolean updatePlayerOnlineStatus(PlayerEntry player, Minecraft mc) {
        boolean changed = false;
        UUID uuid = player.getUUID();
        if (uuid != null) {
            PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(uuid);
            if (playerInfo != null) {
                if (player.setOnlineStatus(PlayerEntry.OnlineStatus.ONLINE)) {
                    changed = true;
                }
                if (player.getName() == null || player.getName().isEmpty() || "Unknown".equals(player.getName())) {
                    player.setName(playerInfo.getProfile().name());
                    changed = true;
                }
            } else {
                if (player.setOnlineStatus(PlayerEntry.OnlineStatus.OFFLINE)) {
                    changed = true;
                }
            }
        } else {
            String name = player.getName();
            if (name != null && !name.isEmpty() && !"Unknown".equals(name)) {
                boolean found = false;
                for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
                    if (info.getProfile().name().equalsIgnoreCase(name)) {
                        if (player.setOnlineStatus(PlayerEntry.OnlineStatus.ONLINE)) {
                            changed = true;
                        }
                        player.setUuid(info.getProfile().id().toString());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (player.setOnlineStatus(PlayerEntry.OnlineStatus.OFFLINE)) {
                        changed = true;
                    }
                }
            } else {
                if (player.setOnlineStatus(PlayerEntry.OnlineStatus.OFFLINE)) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    public static List<StyleEntry> getStyleEntriesForPlayer(String uuid) {
        List<StyleEntry> entries = new ArrayList<>();
        for (NoteList list : getEnabledNoteLists()) {
            if (list.containsPlayerByUUID(uuid)) {
                if (list.getStyleMode() == StyleMode.WHOLE) {
                    entries.add(new StyleEntry(
                        list.getFormattedWholePrefix(),
                        list.getFormattedWholePlayerNameStyle(),
                        list.getFormattedWholeSuffix(),
                        list.isOverridePlayerName(),
                        list.getPriority()
                    ));
                } else {
                    entries.add(new StyleEntry(
                        list.getFormattedStyleText(),
                        list.getStyleMode(),
                        list.getPriority()
                    ));
                }
            }
        }
        return entries;
    }

    public static List<StyleEntry> getStyleEntriesForPlayerByName(String name) {
        List<StyleEntry> entries = new ArrayList<>();
        for (NoteList list : getEnabledNoteLists()) {
            if (list.containsPlayerByName(name)) {
                if (list.getStyleMode() == StyleMode.WHOLE) {
                    entries.add(new StyleEntry(
                        list.getFormattedWholePrefix(),
                        list.getFormattedWholePlayerNameStyle(),
                        list.getFormattedWholeSuffix(),
                        list.isOverridePlayerName(),
                        list.getPriority()
                    ));
                } else {
                    entries.add(new StyleEntry(
                        list.getFormattedStyleText(),
                        list.getStyleMode(),
                        list.getPriority()
                    ));
                }
            }
        }
        return entries;
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

    public static boolean listNameExists(String name) {
        for (NoteList list : noteLists) {
            if (list.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean listNameExistsExcluding(String name, NoteList exclude) {
        for (NoteList list : noteLists) {
            if (list != exclude && list.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
