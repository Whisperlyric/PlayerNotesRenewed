package dev.wsplrc.playernotesrenewed.client.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.wsplrc.playernotesrenewed.client.gui.screen.PlayerListsScreen;

public class ModMenuImpl implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PlayerListsScreen::new;
    }
}
