/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public abstract class NMSObject {
    private static final WrappedMethod asCraftMirror = CraftReflection.craftItemStack
            .getMethod("asCraftMirror", MinecraftReflection.itemStack.getParent());
    private static Map<String, Class<?>> constructors = new HashMap<>();
    @Setter
    private Object object;
    private boolean cancelled;
    private Player player = null;

    public NMSObject(Object object) {
        Atlas.getInstance().getProfile().start("processor:" + object.getClass().getName());
        this.object = object;
        process(player, ProtocolVersion.getGameVersion());
        Atlas.getInstance().getProfile().stop("processor:" + object.getClass().getName());
    }

    public NMSObject(Object object, Player player) {
        this.object = object;
        this.player = player;
        process(player, ProtocolVersion.getGameVersion());
    }

    public NMSObject(PacketReceiveEvent event) {
        this.object = event.getPacket();
        this.player = event.getPlayer();
    }

    public NMSObject(PacketSendEvent event) {
        this.object = event.getPacket();
        this.player = event.getPlayer();
    }

    public static Object construct(String packet, Object... args) {
        try {
            Class<?> c = constructors.get(packet);
            if (c == null) {
                c = Reflections.getNMSClass(packet).getParent();
                constructors.put(packet, c);
            }
            Object p = c.newInstance();
            Field[] fields = c.getDeclaredFields();
            int failed = 0;
            for (int i = 0; i < args.length; i++) {
                Object o = args[i];
                if (o == null) continue;
                fields[i - failed].setAccessible(true);
                try {
                    fields[i - failed].set(p, o);
                } catch (Exception e) {
                    //attempt to continue
                    failed++;
                }
            }
            return p;
        } catch (Exception e) {
            Bukkit.getLogger().severe("The plugin cannot work as protocol incompatibilities were detected... Disabling...");
            e.printStackTrace();
        }
        return null;
    }

    public static Object construct(String packet, Object arg) {
        try {
            Class<?> c = constructors.get(packet);
            if (c == null) {
                c = Reflections.getNMSClass(packet).getParent();
                constructors.put(packet, c);
            }
            Object p = c.newInstance();
            Field[] fields = c.getDeclaredFields();

            if(arg != null) {
                fields[0].setAccessible(true);
                fields[0].set(p, arg);
            }
            return p;
        } catch (Exception e) {
            Bukkit.getLogger().severe("The plugin cannot work as protocol incompatibilities were detected... Disabling...");
            e.printStackTrace();
        }
        return null;
    }

    public static Object construct(Object obj, String packet, Object... args) {
        try {
            Class<?> c = constructors.get(packet);
            if (c == null) {
                c = Reflections.getNMSClass(packet).getParent();
                constructors.put(packet, c);
            }

            Object p = obj != null ? obj : constructors
                    .computeIfAbsent(packet, Reflection::getMinecraftClass).newInstance();
            Field[] fields = c.getDeclaredFields();
            int failed = 0;
            for (int i = 0; i < args.length; i++) {
                Object o = args[i];
                if (o == null) continue;
                fields[i - failed].setAccessible(true);
                try {
                    fields[i - failed].set(p, o);
                } catch (Exception e) {
                    //attempt to continue
                    failed++;
                }
            }
            return p;
        } catch (Exception e) {
            Bukkit.getLogger().severe("The plugin cannot work as protocol incompatibilities were detected... Disabling...");
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack toBukkitStack(Object nmsStack) {
        return asCraftMirror.invoke(null, nmsStack);
    }

    public static <T> FieldAccessor<T> fetchFieldByName(String className, String name, Class<T> fieldType) {
        return Reflection.getField(Reflection.getMinecraftClass(className), name, fieldType);
    }

    public static <T> FieldAccessor<T> fetchField(String className, Class<T> fieldType, int index) {
        return Reflection.getFieldSafe(className, fieldType, index);
    }

    public static <T> FieldAccessor<T> fetchField(String className, String fieldType, int index) {
        return Reflection.getFieldSafe(className, (Class<T>) Reflection.getClass(fieldType), index);
    }

    public static WrappedField fetchField(WrappedClass wrappedClass, Class<?> type, int index) {
        return wrappedClass.getFieldByType(type, index);
    }

    public static WrappedField fetchField(WrappedClass wrappedClass, String name) {
        return wrappedClass.getFieldByName(name);
    }

    /** Updates the vanilla object with the fields set **/
    public abstract void updateObject();

    public String getPacketName() {
        String name = object.getClass().getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public void set(WrappedField field, Object value) {
        field.set(getObject(), value);
    }

    public void set(FieldAccessor<?> accessor, Object value) {
        accessor.set(getObject(), value);
    }

    public void process(Player player, ProtocolVersion version) {
    }

    public void setPacket(String packet, Object... args) {
        this.object = construct(packet, args);
    }

    public <T> T fetch(FieldAccessor<T> field) {
        return field.get(object);
    }

    public <T> T fetch(WrappedField field) {
        return field.get(object);
    }

    public <T> T fetch(WrappedMethod method) {
        return method.invoke(getObject());
    }

    public <T> T fetch(WrappedMethod method, Object... args) {
        return method.invoke(getObject(), args);
    }

    public <T> T fetch(WrappedField field, Object obj) {
        return field.get(obj);
    }

    public <T> T fetch(FieldAccessor<T> field, Object obj) {
        return field.get(obj);
    }

    public static class Type {
        public static final String WATCHABLE_OBJECT = (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8_5))
                ? "WatchableObject" :
                (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)
                ? "DataWatcher$WatchableObject"
                : "DataWatcher$Item");
        public static final String BASEBLOCKPOSITION = "BaseBlockPosition";
        public static final String CHUNKPOSITION = "ChunkPosition";
        public static final String BLOCKPOSITION = "BlockPosition";
        public static final String ITEMSTACK = Reflection.NMS_PREFIX + ".ItemStack";
        public static final String ENTITY = Reflection.NMS_PREFIX + ".Entity";
        public static final String DATAWATCHER = Reflection.NMS_PREFIX + ".DataWatcher";
        public static final String DATAWATCHEROBJECT = Reflection.NMS_PREFIX + ".DataWatcherObject";
        public static final String CHATMESSAGE = Reflection.NMS_PREFIX + ".ChatMessage";
        public static final String CRAFTITEMSTACK = Reflection.OBC_PREFIX + ".inventory.CraftItemStack";
        public static final String GAMEPROFILE = (Reflection.VERSION.startsWith("v1_7") ? "net.minecraft.util." : "") + "com.mojang.authlib.GameProfile";
        public static final String PROPERTYMAP = (Reflection.VERSION.startsWith("v1_7") ? "net.minecraft.util." : "") + "com.mojang.authlib.PropertyMap";
        public static final String VEC3D = Reflection.NMS_PREFIX + ".Vec3D";
        public static final String PLAYERINFODATA = Reflection.NMS_PREFIX + Server.PLAYER_INFO + ".PlayerInfoData";
    }

    public static class Client {
        private static final String CLIENT = "PacketPlayIn";

        public static final String KEEP_ALIVE = CLIENT + "KeepAlive";
        public static final String FLYING = CLIENT + "Flying";
        public static final String POSITION = FLYING + "$" + CLIENT + "Position";
        public static final String POSITION_LOOK = FLYING + "$" + CLIENT + "PositionLook";
        public static final String LOOK = FLYING + "$" + CLIENT + "Look";
        public static final String LEGACY_POSITION = CLIENT + "Position";
        public static final String LEGACY_POSITION_LOOK = CLIENT + "PositionLook";
        public static final String LEGACY_LOOK = CLIENT + "Look";
        public static final String TRANSACTION = ProtocolVersion.getGameVersion()
                .isOrAbove(ProtocolVersion.v1_17) ? "ServerboundPongPacket" : CLIENT + "Transaction";
        public static final String BLOCK_DIG = CLIENT + "BlockDig";
        public static final String ENTITY_ACTION = CLIENT + "EntityAction";
        public static final String USE_ENTITY = CLIENT + "UseEntity";
        public static final String WINDOW_CLICK = CLIENT + "WindowClick";
        public static final String CUSTOM_PAYLOAD = CLIENT + "CustomPayload";
        public static final String ARM_ANIMATION = CLIENT + "ArmAnimation";
        public static final String BLOCK_PLACE_1_9 = CLIENT + "BlockPlace";
        public static final String BLOCK_PLACE = CLIENT
                + (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9) ? "BlockPlace" : "UseItem");
        public static final String STEER_VEHICLE = CLIENT + "SteerVehicle";
        public static final String HELD_ITEM = CLIENT + "HeldItemSlot";
        public static final String CLIENT_COMMAND = CLIENT + "ClientCommand";
        public static final String CLOSE_WINDOW = CLIENT + "CloseWindow";
        public static final String ABILITIES = CLIENT + "Abilities";
        public static final String TAB_COMPLETE = CLIENT + "TabComplete";
        public static final String CHAT = CLIENT + "Chat";
        public static final String CREATIVE_SLOT = CLIENT + "SetCreativeSlot";
        public static final String SETTINGS = CLIENT + "Settings";
        public static final String ADVANCEMENTS = CLIENT + "Advancements";
    }

    public static class Server {
        private static final String SERVER = "PacketPlayOut";

        public static final String KEEP_ALIVE = SERVER + "KeepAlive";
        public static final String CHAT = SERVER + "Chat";
        public static final String POSITION = SERVER + "Position";
        public static final String TRANSACTION = ProtocolVersion.getGameVersion()
                .isOrAbove(ProtocolVersion.v1_17) ? "ClientboundPingPacket" : SERVER +  "Transaction";
        public static final String NAMED_ENTITY_SPAWN = SERVER + "NamedEntitySpawn";
        public static final String SPAWN_ENTITY_LIVING = SERVER + "SpawnEntityLiving";
        public static final String SPAWN_ENTITY = SERVER + "SpawnEntity";
        public static final String CUSTOM_PAYLOAD = SERVER + "CustomPayload";
        public static final String ENTITY_METADATA = SERVER + "EntityMetadata";
        public static final String ENTITY_VELOCITY = SERVER + "EntityVelocity";
        public static final String ENTITY_DESTROY = SERVER + "EntityDestroy";
        public static final String ATTACH = SERVER + "AttachEntity";

        public static final String ENTITY = SERVER + "Entity";
        public static final String REL_POSITION = ENTITY + "$" + SERVER + "RelEntityMove";
        public static final String REL_POSITION_LOOK = ENTITY + "$" + SERVER + "RelEntityMoveLook";
        public static final String REL_LOOK = ENTITY + "$" + SERVER + "EntityLook";
        public static final String LEGACY_REL_POSITION = SERVER + "RelEntityMove";
        public static final String ENTITY_HEAD_ROTATION = SERVER + "EntityHeadRotation";
        public static final String LEGACY_REL_POSITION_LOOK = SERVER + "RelEntityMoveLook";
        public static final String LEGACY_REL_LOOK = SERVER + "EntityLook";
        public static final String ABILITIES = SERVER + "Abilities";
        public static final String OPEN_WINDOW = SERVER + "OpenWindow";
        public static final String HELD_ITEM = SERVER + "HeldItemSlot";
        public static final String PLAYER_INFO = SERVER + "PlayerInfo";
        public static final String TAB_COMPLETE = SERVER + "TabComplete";
        public static final String RESPAWN = SERVER + "Respawn";
        public static final String COMMANDS = SERVER + "Commands";
        public static final String CLOSE_WINDOW = SERVER + "CloseWindow";
        public static final String ENTITY_EFFECT = SERVER + "EntityEffect";
    }

    public static class Login {
        public static final String HANDSHAKE = "PacketHandshakingInSetProtocol";
        public static final String PING = "PacketStatusInPing";
        public static final String START = "PacketStatusInStart";
    }
}
