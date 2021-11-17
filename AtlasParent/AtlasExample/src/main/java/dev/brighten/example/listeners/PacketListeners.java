package dev.brighten.example.listeners;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.impl.PacketLoginEvent;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.listener.functions.PacketListener;
import cc.funkemunky.api.tinyprotocol.packet.in.*;
import cc.funkemunky.api.tinyprotocol.packet.login.WrappedHandshakingInSetProtocol;
import cc.funkemunky.api.tinyprotocol.packet.login.WrappedStatusInPing;
import cc.funkemunky.api.tinyprotocol.packet.out.*;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.RunUtils;

@Init
public class PacketListeners implements AtlasListener {

    /*private PacketListener flyingListener = Atlas.getInstance().getPacketProcessor()
            .processAsync(Atlas.getInstance(), listener -> {
                WrappedInFlyingPacket packet = new WrappedInFlyingPacket(listener.getPacket(), listener.getPlayer());

                Atlas.getInstance().alog("flying: %s, %s, %s, %.1f, %.1f, %.1f",
                        packet.isGround(), packet.isPos(), packet.isLook(),
                        packet.getX(), packet.getY(), packet.getZ());
            }, Packet.Client.FLYING, Packet.Client.POSITION_LOOK, Packet.Client.LOOK, Packet.Client.POSITION);*/

    @Listen
    public void onLogin(PacketLoginEvent event) {
        switch(event.getPacketType()) {
            case Packet.Login.HANDSHAKE: {
                WrappedHandshakingInSetProtocol packet = new WrappedHandshakingInSetProtocol(event.getPacket());

                Atlas.getInstance().alog("&c%s: &f[%s, %s, %s, %s]", event.getPacketType(),
                        packet.a, packet.enumProtocol, packet.hostname, packet.port);
                break;
            }
            case Packet.Login.PING: {
                WrappedStatusInPing packet = new WrappedStatusInPing(event.getPacket());

                Atlas.getInstance().alog("&c%s: &f[%s]",
                        event.getPacketType(),
                        packet.ping);
                break;
            }
        }
    }

    private PacketListener otherListener = Atlas.getInstance().getPacketProcessor()
            .process(Atlas.getInstance(), listener -> {
                switch(listener.getType()) {
                    case Packet.Client.TRANSACTION: {
                        WrappedInTransactionPacket packet = new WrappedInTransactionPacket(listener.getPacket(), listener.getPlayer());

                        Atlas.getInstance().alog("&c%s: &f[%s, %s, %s]", listener.getType(),
                                packet.getAction(), packet.getId(), packet.isAccept());
                        break;
                    }
                    case Packet.Client.WINDOW_CLICK: {
                        WrappedInWindowClickPacket packet = new WrappedInWindowClickPacket(listener.getPacket(),
                                listener.getPlayer());

                        Atlas.getInstance().alog("&c%s: &f[%s, %s, %s, %s, %s, %s, %s]",
                                listener.getType(),
                                packet.getId(), packet.getAction(), packet.getButton(), packet.getCounter(), packet.getItem(), packet.getMode(), packet.getSlot());
                        break;
                    }
                    case Packet.Client.USE_ENTITY: {
                        WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(listener.getPacket(),
                                listener.getPlayer());

                        Atlas.getInstance().alog(true, "Sending trans");
                        RunUtils.task(() -> {
                            WrappedOutTransaction trans = new WrappedOutTransaction(0, (short)69, false);

                            TinyProtocolHandler.sendPacket(listener.getPlayer(), trans.getObject());
                        });

                        Atlas.getInstance().alog("&c%s: &f[%s, %s, %s, %s]",
                                listener.getType(), packet.getId(), packet.getAction(), packet.getEntity(), packet.getEnumHand());
                        break;
                    }
                }
            }, Packet.Client.TRANSACTION, Packet.Client.WINDOW_CLICK, Packet.Client.USE_ENTITY);

    private PacketListener outListener = Atlas.getInstance().getPacketProcessor()
            .process(Atlas.getInstance(), listener -> {
                switch(listener.getType()) {
                    case Packet.Server.CLOSE_WINDOW: {
                        WrappedOutCloseWindowPacket packet = new WrappedOutCloseWindowPacket(listener.getPacket(), listener.getPlayer());

                        Atlas.getInstance().alog("&c%s: &f[%s]", listener.getType(),
                                packet.id);
                        break;
                    }
                    case Packet.Server.CUSTOM_PAYLOAD: {
                        WrappedOutCustomPayload packet = new WrappedOutCustomPayload(listener.getPacket(),
                                listener.getPlayer());

                        Atlas.getInstance().alog("&c%s: &f[%s, %s]", listener.getType(),
                                packet.getData().length, packet.getTag());
                        break;
                    }
                    case Packet.Server.ENTITY_EFFECT: {
                        WrappedOutEntityEffectPacket packet = new WrappedOutEntityEffectPacket(listener.getPacket(),
                                listener.getPlayer());

                        Atlas.getInstance().alog("&c%s: &f[%s, %s, %s, %s, %s]", listener.getType(),
                                packet.amplifier, packet.duration, packet.effectId, packet.entityId, packet.flags);

                        packet.updateObject();
                        break;
                    }
                }
            }, Packet.Server.CLOSE_WINDOW, Packet.Server.CUSTOM_PAYLOAD, Packet.Server.ENTITY_EFFECT);


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
