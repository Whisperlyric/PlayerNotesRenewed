package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(value = PlayerTabOverlay.class, priority = 1001)
public abstract class ChangeTablist {

    private static final Pattern SERVER_PREFIX_PATTERN = Pattern.compile("^(\\[[^\\]]+\\]\\s*)");
    private static final Pattern PLAYER_NAME_PATTERN = Pattern.compile("^(\\w+)$");

    @Inject(method = "getPlayerInfos", at= @At("RETURN"), cancellable = true)
    public void changeOrder(CallbackInfoReturnable<List<PlayerInfo>> cir){
        if (!Config.changeTabOrder || Minecraft.getInstance().getConnection() == null) return;

        ArrayList<PlayerInfo> topPlayers = new ArrayList<>();
        for (PlayerInfo p : Minecraft.getInstance().getConnection().getListedOnlinePlayers()){
            if (Utils.playerHasPrefixByProfile(p.getProfile())){
                if (p.getProfile().name().equals(Minecraft.getInstance().player.getName().getString()) && !Config.changeTabOrderOwn) continue;
                topPlayers.add(p);
            }
        }
        List<PlayerInfo> list = new ArrayList<>(cir.getReturnValue());
        list.removeAll(topPlayers);
        list.addAll(0,topPlayers);
        cir.setReturnValue(list);
    }

    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    public void applyPrefix(PlayerInfo playerInfo, CallbackInfoReturnable<Component> cir){
        if (!Config.editTablist || Minecraft.getInstance().level == null || cir.getReturnValue() == null ) return;
        if (!Config.showPrefixForOwn && playerInfo.getProfile().name().equals(Minecraft.getInstance().player.getName().getString())) return;

        if (Utils.playerHasPrefixByProfile(playerInfo.getProfile())) {
            String prefixes = Utils.getPlayerPrefixesByProfile(playerInfo.getProfile());
            String suffixes = Utils.getPlayerSuffixesByProfile(playerInfo.getProfile());
            
            if (!prefixes.isEmpty() || !suffixes.isEmpty()) {
                Component original = cir.getReturnValue();
                String originalText = original.getString();
                String playerName = playerInfo.getProfile().name();
                
                Matcher serverMatcher = SERVER_PREFIX_PATTERN.matcher(originalText);
                String serverPrefix = "";
                String textAfterServer = originalText;
                
                if (serverMatcher.find()) {
                    serverPrefix = serverMatcher.group(1);
                    textAfterServer = originalText.substring(serverPrefix.length());
                }
                
                String decoratedPlayerName = buildDecoratedPlayerName(playerName, prefixes, suffixes);
                
                String newText;
                if (!serverPrefix.isEmpty()) {
                    newText = serverPrefix + textAfterServer.replace(playerName, decoratedPlayerName);
                } else {
                    newText = originalText.replace(playerName, decoratedPlayerName);
                }
                
                cir.setReturnValue(Component.literal(newText));
            }
        }
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
