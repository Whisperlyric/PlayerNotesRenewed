package dev.wsplrc.playernotesrenewed.client.mixin;

// TODO v1.0.7: 聊天前后缀支持 - 当前实现未生效，需要进一步研究
// 参见 TODO.md 了解已尝试的方案和技术难点

/*
import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(value = ChatComponent.class, priority = 2000)
public abstract class ChangeChat {

    @Unique
    private boolean playernotes$processing = false;

    private static final Pattern VANILLA_MESSAGE_FORMAT = Pattern.compile("^((-> )?\\[[^<]+] )?<([^>]*)>\\s.+$");

    @Inject(
            method = "addMessage(Lnet/minecraft/network/chat/Component;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void playernotes$modifyChatMessage1(Component message, CallbackInfo ci) {
        if (playernotes$processing) {
            return;
        }
        
        Component modified = modifyMessage(message);
        if (modified != message) {
            playernotes$processing = true;
            ci.cancel();
            ((ChatComponent)(Object)this).addMessage(modified);
            playernotes$processing = false;
        }
    }

    private Component modifyMessage(Component message) {
        if (!Config.showPrefixInChat && !Config.showSuffixInChat) {
            return message;
        }
        
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getConnection() == null) {
            return message;
        }
        
        String text = message.getString();
        if (text.isEmpty()) {
            return message;
        }
        
        Matcher matcher = VANILLA_MESSAGE_FORMAT.matcher(text);
        if (!matcher.matches()) {
            return message;
        }
        
        String playerName = null;
        PlayerInfo matchedPlayer = null;
        
        for (PlayerInfo playerInfo : mc.getConnection().getOnlinePlayers()) {
            String name = playerInfo.getProfile().name();
            if (text.contains("<" + name + ">")) {
                playerName = name;
                matchedPlayer = playerInfo;
                break;
            }
        }
        
        if (playerName == null || matchedPlayer == null) {
            return message;
        }
        
        String prefixes = "";
        String suffixes = "";
        
        if (Config.showPrefixInChat && Utils.playerHasPrefixByProfile(matchedPlayer.getProfile())) {
            prefixes = Utils.getPlayerPrefixesByProfile(matchedPlayer.getProfile());
        }
        
        if (Config.showSuffixInChat && Utils.playerHasPrefixByProfile(matchedPlayer.getProfile())) {
            suffixes = Utils.getPlayerSuffixesByProfile(matchedPlayer.getProfile());
        }
        
        if (prefixes.isEmpty() && suffixes.isEmpty()) {
            return message;
        }
        
        String decoratedPlayerName = buildDecoratedPlayerName(playerName, prefixes, suffixes);
        String newText = text.replace("<" + playerName + ">", "<" + decoratedPlayerName + ">");
        
        return Component.literal(newText);
    }
    
    private String buildDecoratedPlayerName(String playerName, String prefixes, String suffixes) {
        StringBuilder sb = new StringBuilder();
        
        if (!prefixes.isEmpty()) {
            sb.append(prefixes);
            if (!Config.styleAffectPlayerName) {
                sb.append("§r");
            }
        }
        
        sb.append(playerName);
        
        if (!suffixes.isEmpty()) {
            sb.append(suffixes);
        }
        
        return sb.toString();
    }
}
*/
