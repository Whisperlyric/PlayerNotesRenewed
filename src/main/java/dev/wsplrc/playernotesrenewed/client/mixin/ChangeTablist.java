package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.objects.StyleEntry;
import dev.wsplrc.playernotesrenewed.client.objects.StyleMode;
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
        List<StyleEntry> playerNameStyles = new ArrayList<>();
        List<StyleEntry> wholeStyles = new ArrayList<>();
        
        for (StyleEntry entry : entries) {
            switch (entry.getMode()) {
                case PREFIX:
                    prefixes.add(entry);
                    break;
                case SUFFIX:
                    suffixes.add(entry);
                    break;
                case PLAYER_NAME:
                    playerNameStyles.add(entry);
                    break;
                case WHOLE:
                    wholeStyles.add(entry);
                    break;
            }
        }
        
        prefixes.sort(Comparator.comparingInt(StyleEntry::getPriority));
        suffixes.sort(Comparator.comparingInt(StyleEntry::getPriority));
        playerNameStyles.sort(Comparator.comparingInt(StyleEntry::getPriority));
        wholeStyles.sort(Comparator.comparingInt(StyleEntry::getPriority));
        
        StringBuilder sb = new StringBuilder();
        
        for (StyleEntry entry : wholeStyles) {
            sb.append(entry.getWholePrefix());
            if (entry.isOverridePlayerName()) {
                sb.append(entry.getWholePlayerNameStyle());
            } else {
                sb.append(entry.getWholePlayerNameStyle()).append(playerName);
            }
            sb.append(entry.getWholeSuffix());
        }
        
        for (StyleEntry entry : prefixes) {
            sb.append(entry.getText()).append("§r ");
        }
        
        for (StyleEntry entry : playerNameStyles) {
            sb.append(entry.getText());
        }
        
        if (wholeStyles.isEmpty()) {
            sb.append(playerName);
        }
        
        for (StyleEntry entry : playerNameStyles) {
            sb.append("§r");
        }
        
        for (StyleEntry entry : suffixes) {
            sb.append(" ").append(entry.getText());
        }
        
        return sb.toString();
    }
}
