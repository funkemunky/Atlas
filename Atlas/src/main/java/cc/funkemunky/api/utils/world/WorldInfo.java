package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.utils.RunUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WorldInfo {
    @Getter
    private final UUID worldId;

    private final Int2ObjectMap<Entity> entityMap = new Int2ObjectOpenHashMap<>();
    private final BukkitTask task;

    public WorldInfo(World world) {
        this.worldId = world.getUID();
        task = RunUtils.taskTimer(() -> {
            synchronized (entityMap) {
                entityMap.clear();
                for (Entity entity : world.getEntities()) {
                    entityMap.put(entity.getEntityId(), entity);
                }
            }
        }, 15, 5);
    }

    public synchronized Optional<Entity> getEntity(int id) {
        return Optional.ofNullable(entityMap.get(id));
    }

    //Synchronizing to prevent multiple plugins for colliding reads at the same time
    public synchronized Optional<Entity> getEntityOrLock(int id) {
        //We should hope the entity is not absent, as it will lock the thread for up to 100ms.
        // This will only occur if the entity just spawns in within 5 ticks or is dead within 5 ticks.
        // Very unlikely but could happen, so only use this method if you expect your entity to exist and
        // aren't just doing a random lookup.
        return Optional.ofNullable(entityMap.computeIfAbsent(id, key -> {
            FutureTask<Entity> task = new FutureTask<Entity>(() -> {
                for (Entity entity : Bukkit.getWorld(worldId).getEntities()) {
                    if(entity.getEntityId() == id) {
                        return entity;
                    }
                }
                return null;
            });

            RunUtils.task(task);

            try {
                return task.get(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                Bukkit.getLogger().warning("Entity with id" + id + " in world " + worldId.toString()
                        + " could not be found within 100ms.");
            }

            return null;
        }));
    }

    public Set<Entity> getEntities() {
        //Creating new instance since objects are cleared every 5 ticks and it's important to keep object
        //redundancy without having to rely on the JVM to do it for us
        return new HashSet<>(entityMap.values());
    }

    public void shutdown() {
        task.cancel();
        entityMap.clear();
    }
}
