package walksy.optimizer.mixin;

import walksy.optimizer.handler.ClientSideCrystalHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {


    @ModifyArgs(method = "onEntitySpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addEntity(Lnet/minecraft/entity/Entity;)V"))
    public void onEntitySpawn(Args args)
    {
        Entity entity = args.get(0);

        if (entity instanceof EndCrystalEntity crystal)
        {
            ClientSideCrystalHandler.handleIncomingServerEntity(crystal, MinecraftClient.getInstance().world);
        }
    }
}
