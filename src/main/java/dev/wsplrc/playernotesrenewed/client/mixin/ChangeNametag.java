package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.objects.StyleEntry;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(value = EntityRenderer.class, priority = 1001)
public class ChangeNametag {

    @Inject(method = "getNameTag", at = @At("RETURN"), cancellable = true)
    public void edit(Entity entity, CallbackInfoReturnable<Component> cir) {
        if (!(entity instanceof Player p)) return;
        if (!Config.editNameTags) return;

        if (Utils.playerHasPrefixByProfile(p.getGameProfile())) {
            List<StyleEntry> entries = Utils.getStyleEntriesByProfile(p.getGameProfile());
            
            if (!entries.isEmpty()) {
                String playerName = p.getGameProfile().name();
                String decoratedPlayerName = buildDecoratedPlayerName(playerName, entries);
                
                String originalText = cir.getReturnValue().getString();
                String newText = originalText.replace(playerName, decoratedPlayerName);
                
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
