package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent")
public abstract class ChangeChat {

    private static final Pattern VANILLA_MESSAGE_FORMAT = Pattern.compile("^((-> )?\\[[^<]+] )?<([^>]*)>\\s.+$");
    private static final Pattern PARSEABLE_MESSAGE_KEYS = Pattern.compile("chat.type.(text|team.(text|sent))");

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
        
        if (message.getContents() instanceof TranslatableContents ttc && PARSEABLE_MESSAGE_KEYS.matcher(ttc.getKey()).matches()) {
            return rebuildTranslatableMessage(ttc, decoratedPlayerName);
        } else {
            String newText = text.replace("<" + playerName + ">", "<" + decoratedPlayerName + ">");
            return Component.literal(newText);
        }
    }
    
    private Component rebuildTranslatableMessage(TranslatableContents ttc, String decoratedPlayerName) {
        boolean team = ttc.getKey().contains("team");
        StringBuilder sb = new StringBuilder();
        
        if (team) {
            if (ttc.getKey().endsWith("sent")) {
                sb.append("-> ");
            }
            Object teamArg = ttc.getArgs()[0];
            String teamName = (teamArg instanceof Component c) ? c.getString() : String.valueOf(teamArg);
            sb.append(teamName).append(" ");
        }
        
        sb.append("<").append(decoratedPlayerName).append("> ");
        
        int contentIndex = team ? 2 : 1;
        if (ttc.getArgs().length > contentIndex) {
            Object contentArg = ttc.getArgs()[contentIndex];
            String content = (contentArg instanceof Component c) ? c.getString() : String.valueOf(contentArg);
            sb.append(content);
        }
        
        return Component.literal(sb.toString());
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
