package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.event.custom.PacketRecieveEvent;
import cc.funkemunky.api.event.custom.PacketSendEvent;
import cc.funkemunky.api.event.system.EventManager;
import cc.funkemunky.api.utils.Init;
import lombok.Getter;
import org.bukkit.entity.Player;

public class TinyProtocolHandler {
    @Getter
    private static AbstractTinyProtocol instance;

    public static boolean enabled = true;

    public TinyProtocolHandler() {
        TinyProtocolHandler self = this;
        // 1.8+ and 1.7 NMS have different class paths for their libraries used. This is why we have to separate the two.
        // These feed the packets asynchronously, before Minecraft processes it, into our own methods to process and be used as an API.
        instance = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8) ? new TinyProtocol1_7(Atlas.getInstance()) {
            @Override
            public Object onPacketOutAsync(Player receiver, Object packet) {
                if(enabled) {
                    return self.onPacketOutAsync(receiver, packet);
                } else {
                    return packet;
                }
            }

            @Override
            public Object onPacketInAsync(Player sender, Object packet) {
                if(enabled) {
                    return self.onPacketInAsync(sender, packet);
                } else {
                    return packet;
                }
            }
        } : new TinyProtocol1_8(Atlas.getInstance()) {
            @Override
            public Object onPacketOutAsync(Player receiver, Object packet) {
                if(enabled) {
                    return self.onPacketOutAsync(receiver, packet);
                } else {
                    return packet;
                }
            }

            @Override
            public Object onPacketInAsync(Player sender, Object packet) {
                if(enabled) {
                    return self.onPacketInAsync(sender, packet);
                } else {
                    return packet;
                }
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

        event = (PacketSendEvent) EventManager.callEvent(event);

        return !event.isCancelled() ? event.getPacket() : null;
    }

    public Object onPacketInAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);

        //Converting the later packets into their equivalent, more understandable legacy types.
        packetName = packetName.replaceAll("PacketPlayInUseItem", "PacketPlayInBlockPlace");

        PacketRecieveEvent event = new PacketRecieveEvent(sender, packet, packetName);

        event = (PacketRecieveEvent) EventManager.callEvent(event);

        return !event.isCancelled() ? event.getPacket() : null;
    }
}

