package walksy.optimizer.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.joml.Math;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import walksy.optimizer.factory.ClientCrystalEntity;
import walksy.optimizer.factory.ClientCrystalFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static walksy.optimizer.ClientSidedCrystalOptimizer.log;

public class ClientSideCrystalHandler {

    private static ClientCrystalFactory FACTORY;

    /**
     * Scuffed way of creating a concurrent hash set
     * Allows for thread-safe collections -> safe access + modification
     */

    private static final Set<ClientCrystalEntity> clientSidedCrystals
            = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final Set<Vec3d> positionHistory
            = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final Set<Vec3d> pendingSoundSuppressions
            = Collections.newSetFromMap(new ConcurrentHashMap<>());

    static {
        initializeSystem();
    }

    public ClientSideCrystalHandler() {
        FACTORY = new ClientCrystalFactory();
    }

    private static void initializeSystem() {
        new ClientSideCrystalHandler();
    }

    /**
     * When the server eventually spawns in an end crystal, it would overlap over the client crystal
     * This is because the client crystal is purely client side, the server doesn't know it's there
     *
     * I counter this by checking if the end crystal in which the server is adding to the world is in the same position
     * as another client crystal
     * If so, then I remove the client crystal from memory, and replace it with the end crystal the server spawns in
     */

    public static void handleIncomingServerEntity(EndCrystalEntity incomingCrystal, ClientWorld currentWorld) {
        log("Incoming crystal from server at pos: " + incomingCrystal.getPos());
        for (ClientCrystalEntity clientCrystal : clientSidedCrystals) {
            if (positionsMatch(clientCrystal.getPos(), incomingCrystal.getPos())) {
                triggerDesynchronizationCorrection(clientCrystal, incomingCrystal, currentWorld);
            }
        }
    }

    /**
     * Fixes a bug where if the user destroys a client crystal before the server spawns the end crystal for the client,
     * the client crystal then gets destroyed, but the end crystal spawns in for a brief moment before getting removed
     *
     * This is due to the delay from the server
     * I counter this by tracking the position of a ClientCrystal which the player has just broken
     * Then if the server spawns a crystal on the one of those positions then we cancel this
     *
     * This can be done via ClientPlayNetworkHandler where it attempts to add the entity into the client world
     * We can cancel this as it's purely client side - regardless the end crystal will be present server side
     */

    public static void destroyClientCrystal(ClientCrystalEntity destroyedCrystal, ClientWorld world, boolean save) {
        log("Destroyed Client Crystal at: " + destroyedCrystal.getPos() + " save: " + save);
        if (save) {
            positionHistory.add(destroyedCrystal.getPos());
            playExplosionSound(world, destroyedCrystal.getPos());
        }
        world.removeEntity(destroyedCrystal.getId(), Entity.RemovalReason.KILLED);
    }

    public static void interceptServerCrystalSpawn(EndCrystalEntity crystalEntity, CallbackInfo callbackInfo) {
        Vec3d spawnLocation = crystalEntity.getPos();
        if (positionHistory.contains(spawnLocation)) {
            log("Cancelled crystal spawn at: " + crystalEntity.getPos());
            positionHistory.remove(spawnLocation);
            pendingSoundSuppressions.add(spawnLocation);
            callbackInfo.cancel();
        }
    }

    public static void interceptServerExplosionSound(Vec3d pos, Args args) {
        if (pendingSoundSuppressions.contains(pos)) {
            log("Suppressing server explosion sound at: " + pos);
            args.set(5, 0F); //Set volume to 0 - skip over sound
            pendingSoundSuppressions.remove(pos);
        }
    }


    /**
     * Spawns a client-sided end crystal (stored as an instance of ClientCrystalEntity to be tracked via 'registerClientDestruction')
     */

    public static void trySpawnClientCrystal(ClientWorld simulatedWorld, double posX, double posY, double posZ) {
        //Creates the client crystal
        ClientCrystalEntity clientCrystal = FACTORY.create(simulatedWorld, posX, posY, posZ);

        //Checks if the client crystal already exists
        if (!isAlreadyTracked(clientCrystal)) {
            log("Attempting clientside crystal spawn at: " + clientCrystal.getPos());
            performSpawnSimulation(clientCrystal, simulatedWorld);
        }
    }

    private static boolean isAlreadyTracked(ClientCrystalEntity crystalEntity) {
        return clientSidedCrystals.contains(crystalEntity);
    }

    /**
     * Adds an entity via ClientWorld#addEntity
     * Adds the client crystal to the concurrent hash set
     */

    private static void performSpawnSimulation(ClientCrystalEntity crystalEntity, ClientWorld worldContext) {
        clientSidedCrystals.add(crystalEntity);
        worldContext.addEntity(crystalEntity);
    }

    /**
     * When replacing the client crystal, I need to ensure to set the age of the new crystal to the age of the client crystal
     *
     * EndCrystalEntity holds a variable 'endCrystalAge', which is solely used in the EndCrystalRenderer to help with
     * the rotations of crystals
     *
     * If we don't do this, when the crystal gets replaced the rotations will be wrong
     */

    private static void triggerDesynchronizationCorrection(ClientCrystalEntity clientCrystal, EndCrystalEntity originalCrystal, ClientWorld currentWorld) {
        log("Desynchronization occuring at: " + clientCrystal.getPos());
        clientSidedCrystals.remove(clientCrystal);
        FACTORY.sync(clientCrystal.endCrystalAge, originalCrystal);
        currentWorld.removeEntity(clientCrystal.getId(), Entity.RemovalReason.DISCARDED);
    }

    /**
     * In case the client entity is somehow alive when another explosion happens, we get rid of it to prevent desyncs
     */

    public static void handleNearbyExplosions(Vec3d pos, float power, MinecraftClient client)
    {
        if (client.player == null) return;

        double maxDistance = power * 2;

        int x1 = MathHelper.floor(pos.x - maxDistance - 1.0);
        int x2 = MathHelper.floor(pos.x + maxDistance + 1.0);
        int y1 = MathHelper.floor(pos.y - maxDistance - 1.0);
        int y2 = MathHelper.floor(pos.y + maxDistance + 1.0);
        int z1 = MathHelper.floor(pos.z - maxDistance - 1.0);
        int z2 = MathHelper.floor(pos.z + maxDistance + 1.0);
        List<ClientCrystalEntity> nearCrystals = client.world.getEntitiesByClass(ClientCrystalEntity.class, new Box(x1, y1, z1, x2, y2, z2), Objects::nonNull);

        for (ClientCrystalEntity nearCrystal : nearCrystals)
        {
            double distance = Math.sqrt(nearCrystal.squaredDistanceTo(pos));
            if (distance < maxDistance) {
                destroyClientCrystal(nearCrystal, client.world, false);
            }
        }
    }

    private static boolean positionsMatch(Vec3d pos1, Vec3d pos2) {
        return pos1.equals(pos2);
    }
    private static void playExplosionSound(ClientWorld world, Vec3d pos)
    {
        world.playSound(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                SoundCategory.BLOCKS,
                4.0F,
                (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F,
                false
        );
    }
}
