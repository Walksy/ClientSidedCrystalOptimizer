package walksy.optimizer.factory;

import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClientCrystalEntity extends EndCrystalEntity {

    private final BlockPos interactPos;

    public ClientCrystalEntity(World world, BlockPos interactPos, double x, double y, double z) {
        super(world, x, y, z);
        this.setShowBottom(false);
        this.interactPos = interactPos;
    }

    public BlockPos getInteractPos()
    {
        return this.interactPos;
    }

    /*
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ClientCrystalEntity other)) return false;
        return this.getBlockPos().equals(other.getBlockPos());
    }

    @Override
    public int hashCode() {
        return this.getBlockPos().hashCode();
    }

     */
}
