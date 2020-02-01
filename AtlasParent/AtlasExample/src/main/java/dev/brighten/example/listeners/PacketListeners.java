package dev.brighten.example.listeners;

import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInCustomPayload;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInSteerVehiclePacket;
import cc.funkemunky.api.utils.Init;

@Init
public class PacketListeners implements AtlasListener {

    @Listen
    public void onEvent(PacketReceiveEvent event) {
        switch(event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.POSITION:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LOOK: {
                WrappedInFlyingPacket packet = new WrappedInFlyingPacket
                        (event.getPacket(), event.getPlayer());

                System.out.println("flying: " + packet.isGround() + ", " + packet.isPos() + ", " + packet.isLook());
                break;
            }
            case Packet.Client.STEER_VEHICLE: {
                WrappedInSteerVehiclePacket packet = new WrappedInSteerVehiclePacket
                        (event.getPacket(), event.getPlayer());

                System.out.println(event.getType());
                break;
            }
            case Packet.Client.CUSTOM_PAYLOAD: {
                WrappedInCustomPayload packet = new WrappedInCustomPayload(event);

                System.out.println(event.getType() + ": " + packet.getTag() + ", " + packet.getLength());
                break;
            }
            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket packet = new WrappedInEntityActionPacket
                        (event.getPacket(), event.getPlayer());

                System.out.println(event.getType() + ": " + packet.getAction().name());
                break;
            }
        }
    }

    @Listen
    public void onEvent(PacketSendEvent event) {

    }
}
