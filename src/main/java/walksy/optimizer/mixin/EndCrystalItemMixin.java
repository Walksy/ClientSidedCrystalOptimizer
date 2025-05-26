package walksy.optimizer.mixin;

import walksy.optimizer.handler.ClientSideCrystalHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    public void onUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir)
    {
        ClientWorld world = MinecraftClient.getInstance().world;
        BlockPos blockPos = context.getBlockPos();
        BlockPos spawnPos = blockPos.up();
        double d = spawnPos.getX();
        double e = spawnPos.getY();
        double f = spawnPos.getZ();
        ClientSideCrystalHandler.trySpawnClientCrystal(world, blockPos,d + (double)0.5F, e, f + (double)0.5F);
    }
}
