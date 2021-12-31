/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public abstract class Packet {
    private static Map<String, Class<?>> constructors = new HashMap<>();
    private Object packet;
    private boolean cancelled;

    public Packet(Object packet) {
        this.packet = packet;
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

    public static <T> FieldAccessor<T> fetchField(String className, Class<T> fieldType, int index) {
        return Reflection.getFieldSafe(Reflection.NMS_PREFIX + "." + className, fieldType, index);
    }

    public static boolean isPositionLook(String type) {
        switch (type) {
            case Client.POSITION_LOOK:
            case Client.LEGACY_POSITION_LOOK:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPosition(String type) {
        switch (type) {
            case Client.POSITION:
            case Client.LEGACY_POSITION:
                return true;
            default:
                return false;
        }
    }

    public static boolean isLook(String type) {
        switch (type) {
            case Client.LOOK:
            case Client.LEGACY_LOOK:
                return true;
            default:
                return false;
        }
    }

    public String getPacketName() {
        String name = packet.getClass().getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public void process(Player player, ProtocolVersion version) {
    }

    public void setPacket(String packet, Object... args) {
        this.packet = construct(packet, args);
    }

    public <T> T fetch(FieldAccessor<T> field) {
        return field.get(packet);
    }

    public static class Type {
        public static final String WATCHABLE_OBJECT = Reflection.NMS_PREFIX
                + (ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_8)
                ? ".WatchableObject" : ".DataWatcher$WatchableObject");
        public static final String BASEBLOCKPOSITION = Reflection.NMS_PREFIX + "BaseBlockPosition";
        public static final String ITEMSTACK = Reflection.NMS_PREFIX + ".ItemStack";
        public static final String ENTITY = Reflection.NMS_PREFIX + ".Entity";
        public static final String DATAWATCHER = Reflection.NMS_PREFIX + ".DataWatcher";
        public static final String DATAWATCHEROBJECT = Reflection.NMS_PREFIX + ".DataWatcherObject";
        public static final String CRAFTITEMSTACK = Reflection.OBC_PREFIX + ".inventory.CraftItemStack";
        public static final String GAMEPROFILE = "com.mojang.authlib.GameProfile";
        public static final String PROPERTYMAP = "com.mojang.authlib.PropertyMap";
    }

    public static class Client {
        private static final String CLIENT = "PacketPlayIn";

        public static final String KEEP_ALIVE = CLIENT + "KeepAlive";
        public static final String FLYING = CLIENT + "Flying";

        public static final String POSITION = CLIENT + "Position";
        public static final String POSITION_LOOK = CLIENT + "PositionLook";
        public static final String LOOK = CLIENT + "Look";
        @Deprecated
        public static final String LEGACY_POSITION = FLYING + "$" + CLIENT + "Position";
        @Deprecated
        public static final String LEGACY_POSITION_LOOK = FLYING + "$" + CLIENT + "PositionLook";
        @Deprecated
        public static final String LEGACY_LOOK = FLYING + "$" + CLIENT + "Look";

        public static final String TRANSACTION = CLIENT + "Transaction";
        public static final String BLOCK_DIG = CLIENT + "BlockDig";
        public static final String ENTITY_ACTION = CLIENT + "EntityAction";
        public static final String USE_ENTITY = CLIENT + "UseEntity";
        public static final String WINDOW_CLICK = CLIENT + "WindowClick";
        public static final String STEER_VEHICLE = CLIENT + "SteerVehicle";
        public static final String CUSTOM_PAYLOAD = CLIENT + "CustomPayload";
        public static final String ARM_ANIMATION = CLIENT + "ArmAnimation";
        public static final String BLOCK_PLACE_1_9 = CLIENT + "BlockPlace1_9";
        public static final String BLOCK_PLACE = CLIENT + "BlockPlace";
        public static final String ABILITIES = CLIENT + "Abilities";
        public static final String HELD_ITEM_SLOT = CLIENT + "HeldItemSlot";
        public static final String CLOSE_WINDOW = CLIENT + "CloseWindow";
        public static final String TAB_COMPLETE = CLIENT + "TabComplete";
        public static final String CHAT = CLIENT + "Chat";
        public static final String CREATIVE_SLOT = CLIENT + "SetCreativeSlot";
        public static final String CLIENT_COMMAND = CLIENT + "ClientCommand";
        public static final String SETTINGS = CLIENT + "Settings";
        public static final String ADVANCEMENTS = CLIENT + "Advancements";
        public static final String UPDATE_SIGN = CLIENT + "UpdateSign";
        public static final String TELEPORT_ACCEPT = CLIENT + "TeleportAccept";
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
        public static final String ABILITIES = SERVER + "Abilities";
        public static final String ENTITY_METADATA = SERVER + "EntityMetadata";
        public static final String ENTITY_VELOCITY = SERVER + "EntityVelocity";
        public static final String ENTITY_DESTROY = SERVER + "EntityDestroy";
        public static final String ENTITY_HEAD_ROTATION = SERVER + "EntityHeadRotation";
        public static final String ENTITY_TELEPORT = SERVER + "EntityTeleport";
        public static final String ENTITY = SERVER + "Entity";
        public static final String REL_POSITION = ENTITY + "$" + SERVER + "RelEntityMove";
        public static final String REL_POSITION_LOOK = ENTITY + "$" + SERVER + "RelEntityMoveLook";
        public static final String REL_LOOK = ENTITY + "$" + SERVER + "EntityLook";
        public static final String LEGACY_REL_POSITION = SERVER + "RelEntityMove";
        public static final String LEGACY_REL_POSITION_LOOK = SERVER + "RelEntityMoveLook";
        public static final String LEGACY_REL_LOOK = SERVER + "EntityLook";
        public static final String BLOCK_CHANGE = SERVER + "BlockChange";
        public static final String CLOSE_WINDOW = SERVER + "CloseWindow";
        public static final String HELD_ITEM = SERVER + "HeldItemSlot";
        public static final String TAB_COMPLETE = SERVER + "TabComplete";
        public static final String RESPAWN = SERVER + "Respawn";
        public static final String WORLD_PARTICLE = SERVER + "WorldParticles";
        public static final String COMMANDS = SERVER + "Commands";
        public static final String OPEN_WINDOW = SERVER + "OpenWindow";
        public static final String ENTITY_EFFECT = SERVER + "EntityEffect";
        public static final String SET_SLOT =  SERVER + "SetSlot";
        public static final String EXPLOSION = SERVER + "Explosion";
        public static final String ATTACH = SERVER + "AttachEntity";
    }

    public static class Login {
        public static final String HANDSHAKE = "PacketHandshakingInSetProtocol";
        public static final String PING = "PacketStatusInPing";
        public static final String START = "PacketStatusInStart";
    }

    public static final Set<String> incomingPackets = new HashSet<>(), outgoingPackets = new HashSet<>(),
            allPackets = new HashSet<>();

    static {
        new WrappedClass(Packet.Server.class).getFields().forEach(field -> {
            String packet = field.get(null);
            outgoingPackets.add(packet);
            allPackets.add(packet);
        });

        new WrappedClass(Packet.Client.class).getFields().forEach(field -> {
            String packet = field.get(null);
            incomingPackets.add(packet);
            allPackets.add(packet);
        });

        new WrappedClass(Packet.Login.class).getFields().forEach(field -> {
            String packet = field.get(null);
            allPackets.add(packet);
        });
    }
}
