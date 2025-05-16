package walksy.optimizer.factory;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.EndCrystalEntity;

/**
 * Pretty useless class
 */

public class ClientCrystalFactory {

    public ClientCrystalEntity create(ClientWorld worldContext, double x, double y, double z) {
        return new ClientCrystalEntity(worldContext, x, y, z);
    }

    public void sync(int age, EndCrystalEntity crystal)
    {
        crystal.endCrystalAge = age;
    }
}