package walksy.optimizer.buffer;

import net.minecraft.util.math.BlockPos;
import walksy.optimizer.factory.ClientCrystalEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientCrystalBuffer {

    public static ClientCrystalBuffer createBuffer()
    {
        return new ClientCrystalBuffer();
    }

    private final Map<BlockPos, ClientCrystalEntity> crystalMap = new ConcurrentHashMap<>();

    public boolean add(ClientCrystalEntity crystal, BlockPos interactPosition) {
        return crystalMap.putIfAbsent(interactPosition, crystal) == null;
    }

    public boolean remove(ClientCrystalEntity crystal) {
        return crystalMap.remove(crystal.getInteractPos(), crystal);
    }

    public ClientCrystalEntity get(BlockPos pos) {
        return crystalMap.get(pos);
    }

    public boolean contains(ClientCrystalEntity crystal) {
        return crystalMap.containsKey(crystal.getInteractPos());
    }

    public Iterable<ClientCrystalEntity> all() {
        return crystalMap.values();
    }

    public void clear() {
        crystalMap.clear();
    }
}
