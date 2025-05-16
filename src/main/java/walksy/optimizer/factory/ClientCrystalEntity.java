package walksy.optimizer.factory;

import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.world.World;

public class ClientCrystalEntity extends EndCrystalEntity {

    public ClientCrystalEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.setShowBottom(false);
    }
}
