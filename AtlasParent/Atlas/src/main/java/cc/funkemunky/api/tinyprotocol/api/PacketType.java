package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.tinyprotocol.packet.in.*;
import cc.funkemunky.api.tinyprotocol.packet.in.impl.*;
import cc.funkemunky.api.tinyprotocol.packet.login.WrappedHandshakingInSetProtocol;
import cc.funkemunky.api.tinyprotocol.packet.login.WrappedStatusInPing;
import cc.funkemunky.api.tinyprotocol.packet.login.WrappedStatusInStart;
import cc.funkemunky.api.tinyprotocol.packet.out.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PacketType {

    private static final String clientPrefix = "PacketPlayIn", serverPrefix = "PacketPlayOut";

    public enum Login {

        HANDSHAKE("PacketHandshakingInSetProtocol", WrappedHandshakingInSetProtocol::new),
        PING("PacketStatusInPing", WrappedStatusInPing::new),
        START("PacketStatusInStart", WrappedStatusInStart::new);

        public final String vanillaName;
        public final ProtocolVersion min, max;
        public final Function<Object, NMSObject> wrappedPacket;
        public final String[] aliases;

        Login(String vanillaName, ProtocolVersion min, ProtocolVersion max,
               Function<Object, NMSObject> wrappedPacket, String... aliases) {
            this.vanillaName = vanillaName;
            this.min = min;
            this.max = max;
            this.wrappedPacket = wrappedPacket;
            this.aliases = aliases;
        }

        Login(String vanillaName, ProtocolVersion min,
              Function<Object, NMSObject> wrappedPacket, String... aliases) {
            this(vanillaName, min, ProtocolVersion.v1_16_2, wrappedPacket, aliases);
        }

        Login(String vanillaName, Function<Object, NMSObject> wrappedPacket, String... aliases) {
            this(vanillaName, ProtocolVersion.V1_7, ProtocolVersion.v1_16_2, wrappedPacket, aliases);
        }

        public static Optional<Login> getPacket(String type) {
            return Arrays.stream(Login.values())
                    .filter(cl -> ProtocolVersion.getGameVersion().isOrAbove(cl.min)
                            && ProtocolVersion.getGameVersion().isOrBelow(cl.max)
                            && (cl.vanillaName.equals(type)
                            || Arrays.asList(cl.aliases).contains(type)))
                    .findFirst();
        }
    }

    public enum Server {

        KEEP_ALIVE(serverPrefix + "KeepAlive",
                (player, packet) -> new WrappedOutKeepAlivePacket(packet, player)),
        CHAT(serverPrefix + "Chat", (player, packet) -> new WrappedInChatPacket(packet, player)),
        POSITION(serverPrefix + "Position", (player, packet) -> new WrappedOutPositionPacket(packet, player)),
        TRANSACTION(serverPrefix + "Transaction",
                (player, packet) -> new WrappedOutTransaction(packet, player)),
        NAMED_ENTITY_SPAWN(serverPrefix + "NamedEntitySpawn"),
        SPAWN_ENTITY_LIVING(serverPrefix + "SpawnEntityLiving"),
        SPAWN_ENTITY(serverPrefix + "SpawnEntity"),
        CUSTOM_PAYLOAD(serverPrefix + "CustomPayload",
                (player, packet) -> new WrappedOutCustomPayload(packet, player)),
        ABILITIES(serverPrefix + "Abilities",
                (player, packet) -> new WrappedOutAbilitiesPacket(packet, player)),
        ENTITY_METADATA(serverPrefix + "EntityMetadata",
                (player, packet) -> new WrappedOutEntityMetadata(packet, player)),
        ENTITY_VELOCITY(serverPrefix + "EntityVelocity",
                (player, packet) -> new WrappedOutVelocityPacket(packet, player)),
        ENTITY_DESTROY(serverPrefix + "EntityDestroy"),
        ENTITY_HEAD_ROTATION(serverPrefix + "EntityHeadRotation",
                (player, packet) -> new WrappedOutEntityHeadRotation(packet, player)),
        BLOCK_CHANGE(serverPrefix + "BlockChange",
                (player, packet) -> new WrappedOutBlockChange(packet, player)),
        CLOSE_WINDOW(serverPrefix + "CloseWindow",
                (player, packet) -> new WrappedOutCloseWindowPacket(packet, player)),
        HELD_ITEM(serverPrefix + "HeldItem", (player, packet) -> new WrappedOutHeldItemSlot(packet, player)),
        TAB_COMPLETE(serverPrefix + "TabComplete",
                (player, packet) -> new WrappedOutTabComplete(packet, player)),
        RESPAWN(serverPrefix + "RESPAWN", (player, packet) -> new WrappedOutRespawnPacket(packet, player)),
        WORLD_PARTICLE(serverPrefix + "WorldParticle",
                (player, packet) -> new WrappedPacketPlayOutWorldParticle(packet, player)),
        COMMANDS(serverPrefix + "Commands", ProtocolVersion.V1_13),
        OPEN_WINDOW(serverPrefix + "OpenWindow",
                (player, packet) -> new WrappedOutOpenWindow(packet, player)),
        ENTITY_EFFECT(serverPrefix + "EntityEffect",
                (player, packet) -> new WrappedOutEntityEffectPacket(packet, player)),
        ENTITY(serverPrefix + "Entity", (player, packet) -> new WrappedOutRelativePosition(packet, player)),
        ENTITY_REL_POSITION(serverPrefix + "Entity$" + serverPrefix + "RelEntityMove",
                (player, packet) -> new WrappedOutRelativePosition(packet, player),
                serverPrefix + "RelEntityMove"),
        ENTITY_REL_POSITION_LOOK(serverPrefix + "Entity$" + serverPrefix + "RelEntityMoveLook",
                (player, packet) -> new WrappedOutRelativePosition(packet, player),
                serverPrefix + "RelEntityMovelook"),
        ENTITY_REL_LOOK(serverPrefix + "Entity$" + serverPrefix + "RelEntityLook",
                (player, packet) -> new WrappedOutRelativePosition(packet, player), serverPrefix + "EntityLook"),
        UNKNOWN("N/A");

        public final String vanillaName;
        public final ProtocolVersion min, max;
        public final BiFunction<Player, Object, NMSObject> wrappedPacket;
        public final String[] aliases;

        Server(String vanillaName, ProtocolVersion min, ProtocolVersion max,
               BiFunction<Player, Object, NMSObject> wrappedPacket, String... aliases) {
            this.vanillaName = vanillaName;
            this.min = min;
            this.max = max;
            this.wrappedPacket = wrappedPacket;
            this.aliases = aliases;
        }

        Server(String vanillaName, ProtocolVersion min,
               BiFunction<Player, Object, NMSObject> wrappedPacket, String... aliases) {
            this(vanillaName, min, ProtocolVersion.v1_16_2, wrappedPacket, aliases);
        }

        Server(String vanillaName, BiFunction<Player, Object, NMSObject> wrappedPacket, String... aliases) {
            this(vanillaName, ProtocolVersion.V1_7, ProtocolVersion.v1_16_2, wrappedPacket, aliases);
        }

        Server(String vanillaName, String... aliases) {
            this(vanillaName, (player, packet) -> new GeneralWrapper(packet, player), aliases);
        }

        Server(String vanillaName, ProtocolVersion min, String... aliases) {
            this(vanillaName, min, (player, packet) -> new GeneralWrapper(packet, player), aliases);
        }

        public static Optional<Server> getPacket(String type) {
            return Arrays.stream(Server.values())
                    .filter(cl -> ProtocolVersion.getGameVersion().isOrAbove(cl.min)
                            && ProtocolVersion.getGameVersion().isOrBelow(cl.max)
                            && (cl.vanillaName.equals(type)
                            || Arrays.asList(cl.aliases).contains(type)))
                    .findFirst();
        }
    }

    public enum Client {
        FLYING(clientPrefix + "Flying",
                (player, packet) -> new WrappedInFlyingPacket(packet, player)),

        POSITION(clientPrefix + "Position", (player, packet) -> new WrappedInFlyingPacket(packet, player),
                "PacketPlayInFlying$PacketPlayInPosition"),
        POSITION_LOOK(clientPrefix + "PositionLook",
                (player, packet) -> new WrappedInFlyingPacket(packet, player),
                "PacketPlayInFlying$PacketPlayInPositionLook"),
        LOOK(clientPrefix + "Look", (player, packet) -> new WrappedInFlyingPacket(packet, player),
                "PacketPlayInFlying$PacketPlayInLook"),
        TRANSACTION(clientPrefix + "Transaction",
                (player, packet) -> new WrappedInTransactionPacket(packet, player)),
        BLOCK_DIG(clientPrefix + "BlockDig"),
        ENTITY_ACTION(clientPrefix + "EntityAction",
                (player, packet) -> new WrappedInEntityActionPacket(packet, player)),
        USE_ENTITY(clientPrefix + "UseEntity",
                (player, packet) -> new WrappedInUseEntityPacket(packet, player)),
        WINDOW_CLICK(clientPrefix + "WindowClick",
                (player, packet) -> new WrappedInWindowClickPacket(packet, player)),
        STEER_VEHICLE(clientPrefix + "SteerVehicle",
                (player, packet) -> new WrappedInSteerVehiclePacket(packet, player)),
        CUSTOM_PAYLOAD(clientPrefix + "CustomPayload",
                (player, packet) -> new WrappedInCustomPayload(packet, player)),
        ARM_ANIMATION(clientPrefix + "ArmAnimation",
                (player, packet) -> new WrappedInArmAnimationPacket(packet, player)),
        USE_ITEM(clientPrefix + "BlockPlace",
                (player, packet) -> new WrappedInBlockPlacePacket(packet, player), clientPrefix + "UseItem"),
        BLOCK_PLACE(clientPrefix + "BlockPlace", ProtocolVersion.V1_9,
                (player, packet) -> new WrappedInBlockPlace1_9(packet, player)),
        ABILITIES(clientPrefix + "Abilities",
                (player, packet) -> new WrappedInAbilitiesPacket(packet, player)),
        HELD_ITEM_SLOT(clientPrefix + "HeldItemSlot",
                (player, packet) -> new WrappedInHeldItemSlotPacket(packet, player)),
        CLOSE_WINDOW(clientPrefix + "CloseWindow",
                (player, packet) -> new WrappedInCloseWindowPacket(packet, player)),
        TAB_COMPLETE(clientPrefix + "TabComplete",
                (player, packet) -> new WrappedInTabComplete(packet, player)),
        CHAT(clientPrefix + "Chat", (player, packet) -> new WrappedInChatPacket(packet, player)),
        CREATIVE_SLOT(clientPrefix + "SetCreativeSlot",
                (player, packet) -> new WrappedInSetCreativeSlotPacket(packet, player)),
        CLIENT_COMMAND(clientPrefix + "ClientCommand",
                (player, packet) -> new WrappedInClientCommandPacket(packet, player)),
        SETTINGS(clientPrefix + "Settings", (player, packet) -> new WrappedInSettingsPacket(packet, player)),
        ADVANCEMENTS(clientPrefix + "Advancements", ProtocolVersion.V1_12,
                (player, packet) -> new WrappedInAdvancementsPacket(packet, player)),
        UNKNOWN("N/A");

        public final String vanillaName;
        public final ProtocolVersion min, max;
        public final BiFunction<Player, Object, NMSObject> wrappedPacket;
        public final String[] aliases;

        Client(String vanillaName, ProtocolVersion min, ProtocolVersion max,
               BiFunction<Player, Object, NMSObject> wrappedPacket, String... aliases) {
            this.vanillaName = vanillaName;
            this.min = min;
            this.max = max;
            this.wrappedPacket = wrappedPacket;
            this.aliases = aliases;
        }

        Client(String vanillaName, ProtocolVersion min,
               BiFunction<Player, Object, NMSObject> wrappedPacket, String... aliases) {
            this(vanillaName, min, ProtocolVersion.v1_16_2, wrappedPacket, aliases);
        }

        Client(String vanillaName, BiFunction<Player, Object, NMSObject> wrappedPacket, String... aliases) {
            this(vanillaName, ProtocolVersion.V1_7, ProtocolVersion.v1_16_2, wrappedPacket, aliases);
        }

        Client(String vanillaName, String... aliases) {
            this(vanillaName, (player, packet) -> new GeneralWrapper(packet, player), aliases);
        }

        Client(String vanillaName, ProtocolVersion min, String... aliases) {
            this(vanillaName, min, (player, packet) -> new GeneralWrapper(packet, player), aliases);
        }

        public static Optional<Client> getPacket(String type) {
            return Arrays.stream(Client.values())
                    .filter(cl -> ProtocolVersion.getGameVersion().isOrAbove(cl.min)
                            && ProtocolVersion.getGameVersion().isOrBelow(cl.max)
                            && (cl.vanillaName.equals(type)
                            || Arrays.asList(cl.aliases).contains(type)))
                    .findFirst();
        }
    }
}
