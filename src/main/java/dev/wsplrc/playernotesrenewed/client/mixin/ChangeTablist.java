package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.objects.PrefixEntry;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
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
            List<PrefixEntry> prefixEntries = Utils.getPlayerPrefixEntriesByProfile(playerInfo.getProfile());
            List<PrefixEntry> suffixEntries = Utils.getPlayerSuffixEntriesByProfile(playerInfo.getProfile());
            
            if (!prefixEntries.isEmpty() || !suffixEntries.isEmpty()) {
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
                
                String teamColorPrefix = getTeamColorPrefix(playerInfo);
                String decoratedPlayerName = buildDecoratedPlayerName(playerName, prefixEntries, suffixEntries, teamColorPrefix);
                
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
    
    private String getTeamColorPrefix(PlayerInfo playerInfo) {
        PlayerTeam team = playerInfo.getTeam();
        if (team != null) {
            ChatFormatting color = team.getColor();
            if (color != null) {
                return "§" + color.getChar();
            }
        }
        return "";
    }
    
    private String buildDecoratedPlayerName(String playerName, List<PrefixEntry> prefixEntries, List<PrefixEntry> suffixEntries, String teamColorPrefix) {
        StringBuilder sb = new StringBuilder();
        
        for (PrefixEntry entry : prefixEntries) {
            sb.append(entry.getText()).append(" ");
            if (!entry.isStyleAffectPlayerName()) {
                sb.append("§r").append(teamColorPrefix);
            }
        }
        
        sb.append(playerName);
        
        for (PrefixEntry entry : suffixEntries) {
            sb.append(" ").append(entry.getText());
        }
        
        return sb.toString();
    }
}
