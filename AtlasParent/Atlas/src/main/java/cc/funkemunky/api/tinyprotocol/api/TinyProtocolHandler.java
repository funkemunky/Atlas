package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.BungeeAPI;
import cc.funkemunky.api.events.impl.PacketLoginEvent;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.tinyprotocol.api.packets.AbstractTinyProtocol;
import cc.funkemunky.api.tinyprotocol.api.packets.channelhandler.TinyProtocol1_7;
import cc.funkemunky.api.tinyprotocol.api.packets.channelhandler.TinyProtocol1_8;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TinyProtocolHandler {
    @Getter
    private static AbstractTinyProtocol instance;

    public boolean paused = false;

    public static Map<UUID, Integer> bungeeVersionCache = new HashMap<>();

    public TinyProtocolHandler() {
        // 1.8+ and 1.7 NMS have different class paths for their libraries used. This is why we have to separate the two.
        // These feed the packets asynchronously, before Minecraft processes it, into our own methods to process and be used as an API.

        TinyProtocolHandler self = this;
        // 1.8+ and 1.7 NMS have different class paths for their libraries used. This is why we have to separate the two.
        // These feed the packets asynchronously, before Minecraft processes it, into our own methods to process and be used as an API.
        instance = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8) ?
                new TinyProtocol1_7(Atlas.getInstance()) {
            @Override
            public Object onHandshake(SocketAddress address, Object packet) {
                return self.onHandshake(address, packet);
            }

            @Override
            public Object onPacketOutAsync(Player receiver, Object packet) {
                return self.onPacketOutAsync(receiver, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Object packet) {
                return self.onPacketInAsync(sender, packet);
            }
        } : new TinyProtocol1_8(Atlas.getInstance()) {
            @Override
            public Object onHandshake(SocketAddress address, Object packet) {
                return self.onHandshake(address, packet);
            }

            @Override
            public Object onPacketOutAsync(Player receiver, Object packet) {
                return self.onPacketOutAsync(receiver, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Object packet) {
                return self.onPacketInAsync(sender, packet);
            }
        };
    }

    // Purely for making the code cleaner
    public static void sendPacket(Player player, Object obj) {
        Object packet;

        if(obj instanceof NMSObject) packet = ((NMSObject) obj).getObject();
        else packet = obj;

        instance.sendPacket(player, packet);
    }

    public static ProtocolVersion getProtocolVersion(Player player) {
        if(Atlas.getInstance().getBungeeManager().isBungee()) {
            int version = bungeeVersionCache
                    .computeIfAbsent(player.getUniqueId(), key -> {
                        int bv = BungeeAPI.getPlayerVersion(player);
                        bungeeVersionCache.put(key, bv);
                        return bv;
                    });

            if(version != -1) return ProtocolVersion.getVersion(version);
        }
        return ProtocolVersion.getVersion(instance.getProtocolVersion(player));
    }

    public Object onPacketOutAsync(Player sender, Object packet) {
        if(!paused && sender != null && packet != null) {
            String name = packet.getClass().getName();
            int index = name.lastIndexOf(".");
            String packetName = name.substring(index + 1);

            PacketSendEvent event = new PacketSendEvent(sender, packet, packetName);

            //EventManager.callEvent(new cc.funkemunky.api.event.custom.PacketSendEvent(sender, packet, packetName));

            Atlas.getInstance().getEventManager().callEvent(event);
            return !event.isCancelled() ? event.getPacket() : null;
        } else return packet;
    }

    public Object onPacketInAsync(Player sender, Object packet) {
        if(!paused && sender != null && packet != null) {
            String name = packet.getClass().getName();
            int index = name.lastIndexOf(".");

            String packetName = name.substring(index + 1)
                    .replace(Packet.Client.LEGACY_LOOK, Packet.Client.LOOK)
                    .replace(Packet.Client.LEGACY_POSITION, Packet.Client.POSITION)
                    .replace(Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.POSITION_LOOK);

            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                packetName = packetName.replace("PacketPlayInBlockPlace",
                        "PacketPlayInBlockPlace1_9")
                        .replace("PacketPlayInUseItem", "PacketPlayInBlockPlace");
            }

            //Bukkit.broadcastMessage(packetName);

            PacketReceiveEvent event = new PacketReceiveEvent(sender, packet, packetName);

            Atlas.getInstance().getEventManager().callEvent(event);

            return !event.isCancelled() ? event.getPacket() : null;
        } return packet;
    }

    public Object onHandshake(SocketAddress address, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);

        PacketLoginEvent event = new PacketLoginEvent(address, packet, packetName);

        Atlas.getInstance().getEventManager().callEvent(event);

        return !event.isCancelled() ? event.getPacket() : null;
    }
}

