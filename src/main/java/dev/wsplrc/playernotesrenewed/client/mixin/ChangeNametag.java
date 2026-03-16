package dev.wsplrc.playernotesrenewed.client.mixin;

import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderer.class, priority = 1001)
public class ChangeNametag {

    @Inject(method = "getNameTag", at = @At("RETURN"), cancellable = true)
    public void edit(Entity entity, CallbackInfoReturnable<Component> cir) {
        if (!(entity instanceof Player p)) return;
        if (!Config.editNameTags) return;

        if (Utils.playerHasPrefixByProfile(p.getGameProfile())) {
            String prefixes = Utils.getPlayerPrefixesByProfile(p.getGameProfile());
            String suffixes = Utils.getPlayerSuffixesByProfile(p.getGameProfile());
            
            if (!prefixes.isEmpty() || !suffixes.isEmpty()) {
                MutableComponent result = Component.literal("");
                if (!prefixes.isEmpty()) {
                    result.append(Component.literal(prefixes));
                }
                if (!Config.styleAffectPlayerName) {
                    result.append(Component.literal("§r"));
                }
                result.append(cir.getReturnValue());
                if (!suffixes.isEmpty()) {
                    result.append(Component.literal(suffixes));
                }
                cir.setReturnValue(result);
            }
        }
    }
}
