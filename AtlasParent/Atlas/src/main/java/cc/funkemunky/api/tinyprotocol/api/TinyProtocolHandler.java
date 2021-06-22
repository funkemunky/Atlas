package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.impl.PacketLoginEvent;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.handlers.protocolsupport.ProtocolAPI;
import cc.funkemunky.api.tinyprotocol.api.packets.AbstractTinyProtocol;
import cc.funkemunky.api.tinyprotocol.api.packets.channelhandler.TinyProtocol1_7;
import cc.funkemunky.api.tinyprotocol.api.packets.channelhandler.TinyProtocol1_8;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Deprecated
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

    @Deprecated
    public static void sendPacket(Player player, Object obj) {
        Object packet;

        if(obj instanceof NMSObject) packet = ((NMSObject) obj).getObject();
        else packet = obj;

        instance.sendPacket(player, packet);
    }

    @Deprecated
    public static ProtocolVersion getProtocolVersion(Player player) {
        return ProtocolVersion.getVersion(ProtocolAPI.INSTANCE.getPlayerVersion(player));
    }

    public Object onPacketOutAsync(Player sender, Object packet) {
        return packet;
    }

    public Object onPacketInAsync(Player sender, Object packet) {
        return packet;
    }

    public Object onHandshake(SocketAddress address, Object packet) {
        return packet;
    }
}

