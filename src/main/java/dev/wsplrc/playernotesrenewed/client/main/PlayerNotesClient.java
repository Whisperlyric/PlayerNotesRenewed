package dev.wsplrc.playernotesrenewed.client.main;

import dev.wsplrc.playernotesrenewed.PlayerNotesRenewed;
import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.config.NoteListManager;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class PlayerNotesClient implements ClientModInitializer {

    private static int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        MidnightConfig.init("playernotes", Config.class);

        PlayerNotesRenewed.KEYBINDS.forEach(KeyBindingHelper::registerKeyBinding);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerNotesRenewed.afterClientTick(client);
            if (client.level != null && client.player != null) {
                tickCounter++;
                int intervalTicks = Config.autoUpdateInterval * 20;
                if (tickCounter >= intervalTicks) {
                    tickCounter = 0;
                    NoteListManager.updateAllOnlineStatus();
                }
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            NoteListManager.updateAllOnlineStatus();
            tickCounter = 0;
        });

        NoteListManager.load();
    }
}
