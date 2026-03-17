package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.objects.PrefixEntry;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EntityRenderer.class, priority = 1001)
public class ChangeNametag {

    @Inject(method = "getNameTag", at = @At("RETURN"), cancellable = true)
    public void edit(Entity entity, CallbackInfoReturnable<Component> cir) {
        if (!(entity instanceof Player p)) return;
        if (!Config.editNameTags) return;

        if (Utils.playerHasPrefixByProfile(p.getGameProfile())) {
            List<PrefixEntry> prefixEntries = Utils.getPlayerPrefixEntriesByProfile(p.getGameProfile());
            List<PrefixEntry> suffixEntries = Utils.getPlayerSuffixEntriesByProfile(p.getGameProfile());
            
            if (!prefixEntries.isEmpty() || !suffixEntries.isEmpty()) {
                String playerName = p.getGameProfile().name();
                String decoratedPlayerName = buildDecoratedPlayerName(playerName, prefixEntries, suffixEntries);
                
                String originalText = cir.getReturnValue().getString();
                String newText = originalText.replace(playerName, decoratedPlayerName);
                
                cir.setReturnValue(Component.literal(newText));
            }
        }
    }
    
    private String buildDecoratedPlayerName(String playerName, List<PrefixEntry> prefixEntries, List<PrefixEntry> suffixEntries) {
        StringBuilder sb = new StringBuilder();
        
        for (PrefixEntry entry : prefixEntries) {
            sb.append(entry.getText()).append(" ");
            if (!entry.isStyleAffectPlayerName()) {
                sb.append("§r");
            }
        }
        
        sb.append(playerName);
        
        for (PrefixEntry entry : suffixEntries) {
            sb.append(" ").append(entry.getText());
        }
        
        return sb.toString();
    }
}
