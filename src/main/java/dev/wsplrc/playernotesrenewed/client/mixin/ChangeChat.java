package dev.wsplrc.playernotesrenewed.client.mixin;

import com.google.common.collect.Lists;
import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
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
        
        Style style = message.getStyle();
        MutableComponent content = Component.empty().setStyle(style);
        
        if (message.getContents() instanceof TranslatableContents ttc && PARSEABLE_MESSAGE_KEYS.matcher(ttc.getKey()).matches()) {
            boolean team = ttc.getKey().contains("team");
            
            if (team) {
                if (ttc.getKey().endsWith("sent")) {
                    content.append(Component.literal("-> ").setStyle(style));
                }
                Object teamArg = ttc.getArgs()[0];
                MutableComponent teamComponent = (teamArg instanceof Component c) ? c.copy() : Component.literal(String.valueOf(teamArg));
                content.append(teamComponent);
                content.append(Component.literal(" "));
            }
            
            MutableComponent playerNameComponent = buildDecoratedPlayerName(playerName, prefixes, suffixes);
            content.append(Component.literal("<").setStyle(style))
                   .append(playerNameComponent)
                   .append(Component.literal("> ").setStyle(style));
            
            int contentIndex = team ? 2 : 1;
            if (ttc.getArgs().length > contentIndex) {
                Object contentArg = ttc.getArgs()[contentIndex];
                if (contentArg instanceof Component c) {
                    content.append(c.copy());
                } else {
                    content.append(Component.literal(String.valueOf(contentArg)));
                }
            }
        } else {
            List<Component> parts = Lists.asList(message.plainCopy().setStyle(style), message.getSiblings().toArray(new Component[0]));
            
            Component firstPart = parts.stream()
                .filter(p -> p.getString().contains(">"))
                .findFirst()
                .orElse(null);
            
            if (firstPart != null) {
                String[] split = firstPart.getString().split(">", 2);
                String beforeBracket = split[0];
                String afterBracket = split.length > 1 ? split[1] : "";
                
                int bracketIndex = beforeBracket.indexOf("<");
                String prefix = bracketIndex > 0 ? beforeBracket.substring(0, bracketIndex) : "";
                
                if (!prefix.isEmpty()) {
                    content.append(Component.literal(prefix).setStyle(firstPart.getStyle()));
                }
                
                MutableComponent playerNameComponent = buildDecoratedPlayerName(playerName, prefixes, suffixes);
                content.append(Component.literal("<").setStyle(firstPart.getStyle()))
                       .append(playerNameComponent)
                       .append(Component.literal(">").setStyle(firstPart.getStyle()));
                
                if (!afterBracket.isEmpty()) {
                    content.append(Component.literal(afterBracket).setStyle(firstPart.getStyle()));
                }
                
                for (int i = parts.indexOf(firstPart) + 1; i < parts.size(); i++) {
                    content.append(parts.get(i));
                }
            } else {
                return message;
            }
        }
        
        return content;
    }
    
    private MutableComponent buildDecoratedPlayerName(String playerName, String prefixes, String suffixes) {
        MutableComponent nameComponent = Component.literal(playerName);
        
        if (!prefixes.isEmpty() || !suffixes.isEmpty()) {
            MutableComponent decorated = Component.empty();
            
            if (!prefixes.isEmpty()) {
                decorated.append(Component.literal(prefixes));
            }
            
            if (!Config.styleAffectPlayerName) {
                decorated.append(Component.literal("§r"));
            }
            
            decorated.append(nameComponent);
            
            if (!suffixes.isEmpty()) {
                decorated.append(Component.literal(suffixes));
            }
            
            return decorated;
        }
        
        return nameComponent;
    }
}
