package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.KLocation;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class EntityData {
    private static Map<EntityType, CollisionBox> entityBounds = new HashMap<>();

    private static WrappedClass entity = Reflections.getNMSClass("Entity"), entitySize;
    private static WrappedField fieldWidth, fieldLength, fieldSize;

    public static CollisionBox bounds(Entity entity) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_14)) {
            return entityBounds.computeIfAbsent(entity.getType(), type -> {
                Object ventity = CraftReflection.getEntity(entity);

                //We cast this as a float since the fields are floats.
                val box = new SimpleCollisionBox(new Vector(), (float)fieldSize.get(ventity),
                        (float)fieldLength.get(ventity));

                entityBounds.put(type, box);
                return box;
            }).copy();
        } else {
            Object ventity = CraftReflection.getEntity(entity);
            Object size = fieldSize.get(ventity);
            //We cast this as a float since the fields are floats.
            return new SimpleCollisionBox(new Vector(), (float)fieldWidth.get(size), (float)fieldLength.get(size));
        }
    }

    public static CollisionBox getEntityBox(Location location, Entity entity) {
        return bounds(entity).offset(location.getX(), location.getY(), location.getZ());
    }

    public static CollisionBox getEntityBox(Vector vector, Entity entity) {
        return bounds(entity).offset(vector.getX(), vector.getY(), vector.getZ());
    }

    public static CollisionBox getEntityBox(KLocation location, Entity entity) {
        return bounds(entity).offset(location.x, location.y, location.z);
    }

    static {
        entityBounds.put(EntityType.WOLF, new SimpleCollisionBox(new Vector(), 0.62, 8));
        entityBounds.put(EntityType.SHEEP, new SimpleCollisionBox(new Vector(), 0.9, 1.3));
        entityBounds.put(EntityType.COW, new SimpleCollisionBox(new Vector(), 0.9, 1.3));
        entityBounds.put(EntityType.PIG, new SimpleCollisionBox(new Vector(), 0.9, 0.9));
        entityBounds.put(EntityType.MUSHROOM_COW, new SimpleCollisionBox(new Vector(), 0.9, 1.3));
        entityBounds.put(EntityType.WITCH, new SimpleCollisionBox(new Vector(), 0.62, 1.95));
        entityBounds.put(EntityType.BLAZE, new SimpleCollisionBox(new Vector(), 0.62, 1.8));
        entityBounds.put(EntityType.PLAYER, new SimpleCollisionBox(new Vector(), 0.6, 1.8));
        entityBounds.put(EntityType.VILLAGER, new SimpleCollisionBox(new Vector(), 0.62, 1.8));
        entityBounds.put(EntityType.CREEPER, new SimpleCollisionBox(new Vector(), 0.62, 1.8));
        entityBounds.put(EntityType.GIANT, new SimpleCollisionBox(new Vector(), 3.6, 10.8));
        entityBounds.put(EntityType.SKELETON, new SimpleCollisionBox(new Vector(), 0.62, 1.8));
        entityBounds.put(EntityType.ZOMBIE, new SimpleCollisionBox(new Vector(), 0.62, 1.8));
        entityBounds.put(EntityType.SNOWMAN, new SimpleCollisionBox(new Vector(), 0.7, 1.9));
        entityBounds.put(EntityType.HORSE, new SimpleCollisionBox(new Vector(), 1.4, 1.6));
        entityBounds.put(EntityType.ENDER_DRAGON, new SimpleCollisionBox(new Vector(), 3, 1.5));
        entityBounds.put(EntityType.ENDERMAN, new SimpleCollisionBox(new Vector(), 0.62, 2.9));
        entityBounds.put(EntityType.CHICKEN, new SimpleCollisionBox(new Vector(), 0.4, 0.7));
        entityBounds.put(EntityType.OCELOT, new SimpleCollisionBox(new Vector(), 0.62, 0.7));
        entityBounds.put(EntityType.SPIDER, new SimpleCollisionBox(new Vector(), 1.4, 0.9));
        entityBounds.put(EntityType.WITHER, new SimpleCollisionBox(new Vector(), 0.9, 3.5));
        entityBounds.put(EntityType.IRON_GOLEM, new SimpleCollisionBox(new Vector(), 1.4, 2.9));
        entityBounds.put(EntityType.GHAST, new SimpleCollisionBox(new Vector(), 4, 4));

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_14)) {
            fieldWidth = entity.getFieldByName("width");
            fieldLength = entity.getFieldByName("length");
        } else {
            entitySize = Reflections.getNMSClass("EntitySize");
            fieldWidth = entitySize.getFieldByName("width");
            fieldLength = entitySize.getFieldByName("length");
            fieldSize = entity.getFieldByType(entitySize.getParent(), 0);
        }
    }
}
