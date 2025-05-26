package walksy.optimizer.mixin;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @ModifyArgs(method = "onExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    public void playExplosionSound(Args args)
    {
        Vec3d position = new Vec3d(args.get(0), args.get(1), args.get(2));
        ClientSideCrystalHandler.interceptServerExplosionSound(position, args);
    }
}
