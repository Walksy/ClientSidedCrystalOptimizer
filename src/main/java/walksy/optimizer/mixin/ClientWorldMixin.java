package walksy.optimizer.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
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

    @Inject(method = "addEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ClientEntityManager;addEntity(Lnet/minecraft/world/entity/EntityLike;)V"), cancellable = true)
    public void addEntity(Entity entity, CallbackInfo ci)
    {
        if (entity instanceof EndCrystalEntity c)
        {
            ClientSideCrystalHandler.handleClientWorldCrystalSpawns(c, ci);
        }
    }

    @Inject(method = "handleBlockUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", shift = At.Shift.BEFORE), cancellable = true)
    public void addEntity(BlockPos pos, BlockState state, int flags, CallbackInfo ci)
    {
        ClientSideCrystalHandler.handleBlockUpdates(pos, state);
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci)
    {
        ClientSideCrystalHandler.onDisconnect();
    }
}
