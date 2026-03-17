package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.objects.StyleEntry;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
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
            List<StyleEntry> entries = Utils.getStyleEntriesByProfile(playerInfo.getProfile());
            
            if (!entries.isEmpty()) {
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
                
                String decoratedPlayerName = buildDecoratedPlayerName(playerName, entries);
                
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
    
    private String buildDecoratedPlayerName(String playerName, List<StyleEntry> entries) {
        List<StyleEntry> prefixes = new ArrayList<>();
        List<StyleEntry> suffixes = new ArrayList<>();
        List<StyleEntry> playerNamePrefixes = new ArrayList<>();
        List<StyleEntry> playerNameSuffixes = new ArrayList<>();
        boolean hasWholeStyle = false;
        String wholeStyleText = "";
        
        for (StyleEntry entry : entries) {
            switch (entry.getType()) {
                case PREFIX:
                    prefixes.add(entry);
                    break;
                case SUFFIX:
                    suffixes.add(entry);
                    break;
                case PLAYER_NAME_PREFIX:
                    playerNamePrefixes.add(entry);
                    break;
                case PLAYER_NAME_SUFFIX:
                    playerNameSuffixes.add(entry);
                    break;
                case WHOLE_STYLE:
                    hasWholeStyle = true;
                    wholeStyleText = entry.getText();
                    break;
            }
        }
        
        prefixes.sort(Comparator.comparingInt(StyleEntry::getPriority));
        suffixes.sort(Comparator.comparingInt(StyleEntry::getPriority));
        playerNamePrefixes.sort(Comparator.comparingInt(StyleEntry::getPriority));
        playerNameSuffixes.sort(Comparator.comparingInt(StyleEntry::getPriority).reversed());
        
        StringBuilder sb = new StringBuilder();
        
        if (hasWholeStyle) {
            sb.append(wholeStyleText);
        }
        
        for (StyleEntry entry : prefixes) {
            sb.append(entry.getText()).append(" ");
        }
        
        for (StyleEntry entry : playerNamePrefixes) {
            sb.append(entry.getText());
        }
        
        sb.append(playerName);
        
        for (StyleEntry entry : playerNameSuffixes) {
            sb.append(entry.getText());
        }
        
        for (StyleEntry entry : suffixes) {
            sb.append(" ").append(entry.getText());
        }
        
        return sb.toString();
    }
}
