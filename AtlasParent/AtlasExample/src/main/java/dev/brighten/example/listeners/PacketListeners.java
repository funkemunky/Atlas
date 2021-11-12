package dev.brighten.example.listeners;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.listener.functions.PacketListener;
import cc.funkemunky.api.tinyprotocol.packet.in.*;
import cc.funkemunky.api.utils.Init;

@Init
public class PacketListeners {

    /*private PacketListener flyingListener = Atlas.getInstance().getPacketProcessor()
            .processAsync(Atlas.getInstance(), listener -> {
                WrappedInFlyingPacket packet = new WrappedInFlyingPacket(listener.getPacket(), listener.getPlayer());

                Atlas.getInstance().alog("flying: %s, %s, %s, %.1f, %.1f, %.1f",
                        packet.isGround(), packet.isPos(), packet.isLook(),
                        packet.getX(), packet.getY(), packet.getZ());
            }, Packet.Client.FLYING, Packet.Client.POSITION_LOOK, Packet.Client.LOOK, Packet.Client.POSITION);*/

    private PacketListener otherListener = Atlas.getInstance().getPacketProcessor()
            .processAsync(Atlas.getInstance(), listener -> {
                switch(listener.getType()) {
                    case Packet.Client.BLOCK_PLACE: {
                        WrappedInBlockPlacePacket packet = new WrappedInBlockPlacePacket(listener.getPacket(), listener.getPlayer());

                        Atlas.getInstance().alog("&c%s: &f[%s, %s, %s, %s, %s, %s]", listener.getType(),
                                packet.getBlockPosition(), packet.getFace(), packet.getItemStack(), packet.getVecX(), packet.getVecY(), packet.getVecZ());
                        break;
                    }
                    case Packet.Client.BLOCK_PLACE_1_9: {
                        WrappedInBlockPlace1_9 packet = new WrappedInBlockPlace1_9(listener.getPacket(), listener.getPlayer());

                        Atlas.getInstance().alog("&c%s: &f[%s, %s]", listener.getType(), packet.mainHand, packet.timeStamp);
                        break;
                    }
                }
            }, Packet.Client.BLOCK_PLACE, Packet.Client.BLOCK_PLACE_1_9);



    /*@Listen
    public void onEvent(PacketReceiveEvent event) {
        switch(event.getType()) {
            case Packet.Client.STEER_VEHICLE: {
                //WrappedInSteerVehiclePacket packet = new WrappedInSteerVehiclePacket
                //        (event.getPacket(), event.getPlayer());

                //System.out.println(event.getType());
                break;
            }
            case Packet.Client.CUSTOM_PAYLOAD: {
                //WrappedInCustomPayload packet = new WrappedInCustomPayload(event);

                //System.out.println(event.getType() + ": " + packet.getTag() + ", " + packet.getLength());
                break;
            }
            case Packet.Client.ENTITY_ACTION: {
                //WrappedInEntityActionPacket packet = new WrappedInEntityActionPacket
                //        (event.getPacket(), event.getPlayer());

                //System.out.println(event.getType() + ": " + packet.getAction().name());
                break;
            }
        }
    }

    @Listen
    public void onEvent(PacketSendEvent event) {

    }*/
}
