package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.Atlas;
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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class EntityData {
    private static final EnumMap<EntityType, CollisionBox> entityBounds = new EnumMap<>(EntityType.class);

    private static final WrappedClass entity = Reflections.getNMSClass("Entity");
    private static final WrappedField fieldWidth;
    private static final WrappedField fieldLength;

    public static CollisionBox bounds(Entity entity) {
        return entityBounds.computeIfAbsent(entity.getType(), type -> {
            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_14)) {
                Object ventity = CraftReflection.getEntity(entity);

                //We cast this as a float since the fields are floats.

                return new SimpleCollisionBox(new Vector(), (float)fieldWidth.get(ventity),
                        (float)fieldLength.get(ventity));
            } else {
                val blockBox = Atlas.getInstance().getBlockBoxManager().getBlockBox();
                return new SimpleCollisionBox(new Vector(), blockBox.getWidth(entity), blockBox.getHeight(entity));
            }
        }).copy();
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
        entityBounds.put(EntityType.WOLF, new SimpleCollisionBox(new Vector(), 0.62f, .8f));
        entityBounds.put(EntityType.SHEEP, new SimpleCollisionBox(new Vector(), 0.9f, 1.3f));
        entityBounds.put(EntityType.COW, new SimpleCollisionBox(new Vector(), 0.9f, 1.3f));
        entityBounds.put(EntityType.PIG, new SimpleCollisionBox(new Vector(), 0.9f, 0.9f));
        entityBounds.put(EntityType.MUSHROOM_COW, new SimpleCollisionBox(new Vector(), 0.9f, 1.3f));
        entityBounds.put(EntityType.WITCH, new SimpleCollisionBox(new Vector(), 0.62f, 1.95f));
        entityBounds.put(EntityType.BLAZE, new SimpleCollisionBox(new Vector(), 0.62f, 1.8f));
        entityBounds.put(EntityType.PLAYER, new SimpleCollisionBox(new Vector(), 0.6f, 1.8f));
        entityBounds.put(EntityType.VILLAGER, new SimpleCollisionBox(new Vector(), 0.62f, 1.8f));
        entityBounds.put(EntityType.CREEPER, new SimpleCollisionBox(new Vector(), 0.62f, 1.8f));
        entityBounds.put(EntityType.GIANT, new SimpleCollisionBox(new Vector(), 3.6f, 10.8f));
        entityBounds.put(EntityType.SKELETON, new SimpleCollisionBox(new Vector(), 0.62f, 1.8f));
        entityBounds.put(EntityType.ZOMBIE, new SimpleCollisionBox(new Vector(), 0.62f, 1.8f));
        entityBounds.put(EntityType.SNOWMAN, new SimpleCollisionBox(new Vector(), 0.7f, 1.9f));
        entityBounds.put(EntityType.HORSE, new SimpleCollisionBox(new Vector(), 1.4f, 1.6f));
        entityBounds.put(EntityType.ENDER_DRAGON, new SimpleCollisionBox(new Vector(), 3f, 1.5f));
        entityBounds.put(EntityType.ENDERMAN, new SimpleCollisionBox(new Vector(), 0.62f, 2.9f));
        entityBounds.put(EntityType.CHICKEN, new SimpleCollisionBox(new Vector(), 0.4f, 0.7f));
        entityBounds.put(EntityType.OCELOT, new SimpleCollisionBox(new Vector(), 0.62f, 0.7f));
        entityBounds.put(EntityType.SPIDER, new SimpleCollisionBox(new Vector(), 1.4f, 0.9f));
        entityBounds.put(EntityType.WITHER, new SimpleCollisionBox(new Vector(), 0.9f, 3.5f));
        entityBounds.put(EntityType.IRON_GOLEM, new SimpleCollisionBox(new Vector(), 1.4f, 2.9f));
        entityBounds.put(EntityType.GHAST, new SimpleCollisionBox(new Vector(), 4f, 4f));

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_14)) {
            fieldWidth = entity.getFieldByName("width");
            fieldLength = entity.getFieldByName("length");
        } else {
            WrappedClass entitySize = Reflections.getNMSClass("EntitySize");
            fieldWidth = entitySize.getFieldByName("width");
            fieldLength = entitySize.getFieldByName("length");
        }
    }
}
