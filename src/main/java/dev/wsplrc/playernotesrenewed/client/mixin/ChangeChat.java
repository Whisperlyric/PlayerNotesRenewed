package dev.wsplrc.playernotesrenewed.client.mixin;

// TODO: 聊天栏显示前后缀功能待实现
// 当前mixin注入存在问题，需要进一步研究ChatComponent.addMessage的正确注入方式

/*
import dev.wsplrc.playernotesrenewed.client.config.Config;
import dev.wsplrc.playernotesrenewed.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public abstract class ChangeChat {

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/network/chat/Component;)V",
            at = @At("HEAD"),
            ordinal = 0
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
        
        for (PlayerInfo playerInfo : mc.getConnection().getOnlinePlayers()) {
            String playerName = playerInfo.getProfile().name();
            if (text.contains("<" + playerName + ">")) {
                String prefixes = "";
                String suffixes = "";
                
                if (Config.showPrefixInChat && Utils.playerHasPrefixByProfile(playerInfo.getProfile())) {
                    prefixes = Utils.getPlayerPrefixesByProfile(playerInfo.getProfile());
                }
                
                if (Config.showSuffixInChat && Utils.playerHasPrefixByProfile(playerInfo.getProfile())) {
                    suffixes = Utils.getPlayerSuffixesByProfile(playerInfo.getProfile());
                }
                
                if (!prefixes.isEmpty() || !suffixes.isEmpty()) {
                    String resetCode = Config.styleAffectPlayerName ? "" : "§r";
                    String decoratedName = prefixes + resetCode + playerName + suffixes;
                    String newText = text.replace("<" + playerName + ">", "<" + decoratedName + ">");
                    return Component.literal(newText);
                }
            }
        }
        
        return message;
    }
}
*/
