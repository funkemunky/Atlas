package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.tinyprotocol.api.packets.AbstractTinyProtocol;
import cc.funkemunky.api.tinyprotocol.api.packets.channelhandler.TinyProtocol1_7;
import cc.funkemunky.api.tinyprotocol.api.packets.channelhandler.TinyProtocol1_8;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TinyProtocolHandler {
    @Getter
    private static AbstractTinyProtocol instance;

    public boolean paused = false;

    public TinyProtocolHandler() {
        // 1.8+ and 1.7 NMS have different class paths for their libraries used. This is why we have to separate the two.
        // These feed the packets asynchronously, before Minecraft processes it, into our own methods to process and be used as an API.

        TinyProtocolHandler self = this;
        // 1.8+ and 1.7 NMS have different class paths for their libraries used. This is why we have to separate the two.
        // These feed the packets asynchronously, before Minecraft processes it, into our own methods to process and be used as an API.
        instance = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8) ? new TinyProtocol1_7(Atlas.getInstance()) {
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
    public static void sendPacket(Player player, Object packet) {
        instance.sendPacket(player, packet);
    }

    public static ProtocolVersion getProtocolVersion(Player player) {
        return ProtocolVersion.getVersion(instance.getProtocolVersion(player));
    }

    private boolean didPosition = false;

    public Object onPacketOutAsync(Player sender, Object packet) {
        if(!paused && sender != null) {
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
        if(!paused && sender != null) {
            String name = packet.getClass().getName();
            int index = name.lastIndexOf(".");
            String packetName = name.substring(index + 1).replace("PacketPlayInUseItem", "PacketPlayInBlockPlace")
                    .replace(Packet.Client.LEGACY_LOOK, Packet.Client.LOOK)
                    .replace(Packet.Client.LEGACY_POSITION, Packet.Client.POSITION)
                    .replace(Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.POSITION_LOOK);

            PacketReceiveEvent event = new PacketReceiveEvent(sender, packet, packetName);

            //EventManager.callEvent(new cc.funkemunky.api.event.custom.PacketReceiveEvent(sender, packet, packetName));

            Atlas.getInstance().getEventManager().callEvent(event);

            return !event.isCancelled() ? event.getPacket() : null;
        } return packet;
    }
}

