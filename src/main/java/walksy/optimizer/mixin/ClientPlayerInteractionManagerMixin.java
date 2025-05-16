package walksy.optimizer.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import walksy.optimizer.handler.ClientSideCrystalHandler;
import walksy.optimizer.factory.ClientCrystalEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {


    @Shadow @Final private MinecraftClient client;

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void onCrystalBreak(PlayerEntity player, Entity target, CallbackInfo ci)
    {
        if (target instanceof ClientCrystalEntity c)
        {
            ClientSideCrystalHandler.destroyClientCrystal(c, client.world , true);
        }
    }
}
