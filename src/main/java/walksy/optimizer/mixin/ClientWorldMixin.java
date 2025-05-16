package walksy.optimizer.mixin;

import walksy.optimizer.handler.ClientSideCrystalHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void addEntity(Entity entity, CallbackInfo ci)
    {
        if (entity instanceof EndCrystalEntity c)
        {
            ClientSideCrystalHandler.interceptServerCrystalSpawn(c, ci);
        }
    }
}
