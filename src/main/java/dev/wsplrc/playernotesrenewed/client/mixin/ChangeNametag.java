package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.objects.StyleEntry;
import dev.wsplrc.playernotesrenewed.client.objects.StyleMode;
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
