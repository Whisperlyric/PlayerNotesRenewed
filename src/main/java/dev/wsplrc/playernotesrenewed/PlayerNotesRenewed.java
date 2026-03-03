package dev.wsplrc.playernotesrenewed;

import com.mojang.blaze3d.platform.InputConstants;
import dev.wsplrc.playernotesrenewed.client.gui.screen.PlayerListsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.List;

public class PlayerNotesRenewed {
    
    public static final String MOD_ID = "playernotes";
//    public static final String MOD_NAME = "PlayerNotesRenewed";
    
    public static final KeyMapping.Category CATEGORY = 
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
    
    public static final KeyMapping OPEN_LISTS_KEY = new KeyMapping(
            "key.playernotes.open_lists",
            InputConstants.Type.KEYSYM,
            -1,
            CATEGORY
    );
    
    public static final List<KeyMapping> KEYBINDS = List.of(OPEN_LISTS_KEY);
    
    public static void afterClientTick(Minecraft mc) {
        while (OPEN_LISTS_KEY.consumeClick()) {
            if (mc.player != null) {
                mc.setScreen(new PlayerListsScreen(mc.screen));
            }
        }
    }
}
