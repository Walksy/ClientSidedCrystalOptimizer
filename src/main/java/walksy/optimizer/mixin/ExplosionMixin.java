package walksy.optimizer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.optimizer.handler.ClientSideCrystalHandler;

@Mixin(ExplosionImpl.class)
public abstract class ExplosionMixin {


    @Shadow
    @Final
    private Vec3d pos;

    @Shadow
    public abstract float getPower();

    @Inject(method = "damageEntities", at = @At("HEAD"))
    public void onExplosion(CallbackInfo ci)
    {
        ClientSideCrystalHandler.handleNearbyExplosions(this.pos, this.getPower(), MinecraftClient.getInstance());
    }
}

/* if 1.21.1
@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow
    @Final
    private double x;

    @Shadow
    @Final
    private double y;

    @Shadow
    @Final
    private float power;

    @Shadow
    @Final
    private World world;

    @Shadow
    @Final
    private double z;

    @Inject(method = "affectWorld", at = @At("HEAD"))
    public void onExplosion(boolean particles, CallbackInfo ci)
    {
        PlayerShieldingManager.INSTANCE.onExplosion(this.x, this.y, this.z, this.power, this.world);
    }

}
 */


