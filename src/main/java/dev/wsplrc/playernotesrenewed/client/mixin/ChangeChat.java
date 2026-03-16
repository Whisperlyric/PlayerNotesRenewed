package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(value = ChatComponent.class, priority = 2000)
public abstract class ChangeChat {

    private static final Pattern VANILLA_MESSAGE_FORMAT = Pattern.compile("^((-> )?\\[[^<]+] )?<([^>]*)>\\s.+$");

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    public Component playernotes$modifyChatMessage(Component message) {
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
