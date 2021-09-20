package cc.funkemunky.api.listeners;

import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.impl.PacketLoginEvent;
import cc.funkemunky.api.handlers.protocolsupport.ProtocolAPI;
import cc.funkemunky.api.reflections.impl.BukkitReflection;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.login.WrappedHandshakingInSetProtocol;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumProtocol;
import cc.funkemunky.api.utils.Init;

@Init
public class AtlasConnectionListeners implements AtlasListener {

    private boolean bungeeMode;

    public AtlasConnectionListeners() {
        bungeeMode = BukkitReflection.isBungeeMode();
    }

    @Listen
    public void onEvent(PacketLoginEvent event) {
        if(event.getPacketType().equals(Packet.Login.HANDSHAKE)) {
            WrappedHandshakingInSetProtocol packet = new WrappedHandshakingInSetProtocol(event.getPacket());

            if(event.getAddress().toString().contains("127.0.0.1") && bungeeMode
                    && packet.enumProtocol.equals(WrappedEnumProtocol.LOGIN)) {
                String[] split = packet.hostname.split("\00");

                if (split.length >= 3) {
                    ProtocolAPI.classInstance.protocolVersionByIP.put(split[1], packet.a);
                }
            } else {
                String address = event.getAddress().toString();

                if(address.contains(":")) address = address.split(":")[0];
                address = address.substring(1);

                ProtocolAPI.classInstance.protocolVersionByIP.put(address, packet.a);
            }
        }
    }
}
