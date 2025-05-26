package walksy.optimizer.factory;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Pretty useless class
 */

public class ClientCrystalFactory {

    public ClientCrystalEntity create(ClientWorld worldContext, BlockPos interactPos, double x, double y, double z) {
        return new ClientCrystalEntity(worldContext, interactPos, x, y, z);
    }

    public void sync(int age, EndCrystalEntity crystal)
    {
        crystal.endCrystalAge = age;
    }
}