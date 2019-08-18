/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.MethodInvoker;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static cc.funkemunky.api.tinyprotocol.api.NMSObject.Type.CRAFTITEMSTACK;
import static cc.funkemunky.api.tinyprotocol.api.NMSObject.Type.ITEMSTACK;

@Getter
@Setter
public abstract class NMSObject {
    private static final MethodInvoker asCraftMirror = Reflection.getMethod(CRAFTITEMSTACK, "asCraftMirror", Reflection.getClass(ITEMSTACK));
    private static Map<String, Class<?>> constructors = new HashMap<>();
    @Setter
    private Object object;
    private boolean cancelled;
    private Player player;

    public NMSObject(Object object) {
        Atlas.getInstance().getProfile().start("processor:" + object.getClass().getName());
        this.object = object;
        process(player, ProtocolVersion.getGameVersion());
        Atlas.getInstance().getProfile().stop("processor:" + object.getClass().getName());
    }

    public NMSObject() {

    }

    public NMSObject(Object object, Player player) {
        this.object = object;
        this.player = player;
        process(player, ProtocolVersion.getGameVersion());
    }

    public static Object construct(String packet, Object... args) {
        try {
            Class<?> c = constructors.get(packet);
            if (c == null) {
                c = Reflection.getMinecraftClass(packet);
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
            System.out.println("The plugin cannot work as protocol incompatibilities were detected... Disabling...");
            e.printStackTrace();
        }
        return null;
    }

    public static Object construct(String packet, Object arg) {
        try {
            Class<?> c = constructors.get(packet);
            if (c == null) {
                c = Reflection.getMinecraftClass(packet);
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
            System.out.println("The plugin cannot work as protocol incompatibilities were detected... Disabling...");
            e.printStackTrace();
        }
        return null;
    }

    public static Object construct(Object p, String packet, Object... args) {
        try {
            Class<?> c = constructors.get(packet);
            if (c == null) {
                c = Reflection.getMinecraftClass(packet);
                constructors.put(packet, c);
            }
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
            System.out.println("The plugin cannot work as protocol incompatibilities were detected... Disabling...");
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack toBukkitStack(Object nmsStack) {
        return (ItemStack) asCraftMirror.invoke(null, nmsStack);
    }

    public static <T> FieldAccessor<T> fetchField(String className, Class<T> fieldType, int index) {
        return Reflection.getFieldSafe(Reflection.NMS_PREFIX + "." + className, fieldType, index);
    }

    public static <T> FieldAccessor<T> fetchFieldByName(String className, String name, Class<T> fieldType) {
        return Reflection.getField(Reflection.NMS_PREFIX + "." + className, name, fieldType);
    }

    public static <T> FieldAccessor<T> fetchField(String className, String fieldType, int index) {
        return Reflection.getFieldSafe(Reflection.NMS_PREFIX + "." + className, (Class<T>) Reflection.getClass(fieldType), index);
    }

    public String getPacketName() {
        String name = object.getClass().getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public void process(Player player, ProtocolVersion version) {
    }

    public void setPacket(String packet, Object... args) {
        this.object = construct(packet, args);
    }

    public void setPacketArg(String packet, Object arg) {
        this.object = construct(packet, arg);
    }

    public void setPacket(String packet, Object arg) {
        setPacketArg(packet, arg);
    }

    public <T> T fetch(FieldAccessor<T> field) {
        return field.get(object);
    }

    public <T> T fetch(FieldAccessor<T> field, Object obj) {
        return field.get(obj);
    }

    public static class Type {
        public static final String WATCHABLE_OBJECT = (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8_5)) ? "WatchableObject" : "DataWatcher$WatchableObject";
        public static final String BASEBLOCKPOSITION = "BaseBlockPosition";
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
        public static final String TRANSACTION = CLIENT + "Transaction";
        public static final String BLOCK_DIG = CLIENT + "BlockDig";
        public static final String ENTITY_ACTION = CLIENT + "EntityAction";
        public static final String USE_ENTITY = CLIENT + "UseEntity";
        public static final String WINDOW_CLICK = CLIENT + "WindowClick";
        public static final String CUSTOM_PAYLOAD = CLIENT + "CustomPayload";
        public static final String ARM_ANIMATION = CLIENT + "ArmAnimation";
        public static final String BLOCK_PLACE = CLIENT + "BlockPlace";
        public static final String STEER_VEHICLE = CLIENT + "SteerVehicle";
        public static final String HELD_ITEM = CLIENT + "HeldItemSlot";
        public static final String CLIENT_COMMAND = CLIENT + "ClientCommand";
        public static final String CLOSE_WINDOW = CLIENT + "CloseWindow";
        public static final String ABILITIES = CLIENT + "Abilities";
        public static final String TAB_COMPLETE = CLIENT + "TabComplete";
        public static final String CHAT = CLIENT + "Chat";
    }

    public static class Server {
        private static final String SERVER = "PacketPlayOut";

        public static final String KEEP_ALIVE = SERVER + "KeepAlive";
        public static final String CHAT = SERVER + "Chat";
        public static final String POSITION = SERVER + "Position";
        public static final String TRANSACTION = SERVER + "Transaction";
        public static final String NAMED_ENTITY_SPAWN = SERVER + "NamedEntitySpawn";
        public static final String SPAWN_ENTITY_LIVING = SERVER + "SpawnEntityLiving";
        public static final String SPAWN_ENTITY = SERVER + "SpawnEntity";
        public static final String CUSTOM_PAYLOAD = SERVER + "CustomPayload";
        public static final String ENTITY_METADATA = SERVER + "EntityMetadata";
        public static final String ENTITY_VELOCITY = SERVER + "EntityVelocity";
        public static final String ENTITY_DESTROY = SERVER + "EntityDestroy";

        public static final String ENTITY = SERVER + "Entity";
        public static final String REL_POSITION = ENTITY + "$" + SERVER + "EntityMove";
        public static final String REL_POSITION_LOOK = ENTITY + "$" + SERVER + "EntityMoveLook";
        public static final String REL_LOOK = ENTITY + "$" + SERVER + "EntityLook";
        public static final String LEGACY_REL_POSITION = SERVER + "EntityMove";
        public static final String LEGACY_REL_POSITION_LOOK = SERVER + "EntityMoveLook";
        public static final String LEGACY_REL_LOOK = SERVER + "EntityLook";
        public static final String ABILITIES = SERVER + "Abilities";
        public static final String OPEN_WINDOW = SERVER + "OpenWindow";
        public static final String HELD_ITEM = SERVER + "HeldItemSlot";
        public static final String PLAYER_INFO = SERVER + "PlayerInfo";
        public static final String TAB_COMPLETE = SERVER + "TabComplete";
        public static final String RESPAWN = SERVER + "Respawn";
    }
}
