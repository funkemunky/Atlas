package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.event.custom.PacketRecieveEvent;
import cc.funkemunky.api.event.custom.PacketSendEvent;
import cc.funkemunky.api.event.system.EventManager;
import io.netty.channel.Channel;
import lombok.Getter;
import org.bukkit.entity.Player;

public class TinyProtocolHandler {
    @Getter
    private static AbstractTinyProtocol instance;

    public TinyProtocolHandler() {
        TinyProtocolHandler self = this;
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

    public static int getProtocolVersion(Player player) {
        return instance.getProtocolVersion(player);
    }

    public Object onPacketOutAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);

        PacketSendEvent event = new PacketSendEvent(sender, packet, packetName);
        EventManager.callEvent(event);

        return !event.isCancelled() ? event.getPacket() : null;
    }

    public Object onPacketInAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);

        //Converting the later packets into their equivalent, more understandable legacy types.
        packetName = packetName.replaceAll("PacketPlayInUseItem", "PacketPlayInBlockPlace");

        PacketRecieveEvent event = new PacketRecieveEvent(sender, packet, packetName);

        EventManager.callEvent(event);

        return !event.isCancelled() ? packet : null;
    }
}

