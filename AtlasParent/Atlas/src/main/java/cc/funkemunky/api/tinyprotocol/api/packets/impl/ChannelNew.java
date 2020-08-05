package cc.funkemunky.api.tinyprotocol.api.packets.impl;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.PacketProcessor;
import cc.funkemunky.api.tinyprotocol.api.PacketType;
import cc.funkemunky.api.tinyprotocol.api.packets.ChannelListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ChannelNew extends ChannelListener {

    @Override
    public void inject(Player player) {
        Atlas.getInstance().getService().execute(() -> {
            Channel channel = getChannel(player);

            if(channel == null) return;

            Listen listen = (Listen) channel.pipeline().get(ChannelListener.handle);

            if(listen == null) {
                listen = new Listen(player);

                if(channel.pipeline().get(ChannelListener.handle) != null) {
                    channel.pipeline().remove(ChannelListener.handle);
                }
                channel.pipeline().addBefore("packet_handler", ChannelListener.handle, listen);
            }
        });
    }

    @Override
    public void uninject(Player player) {
        Atlas.getInstance().getService().execute(() -> {
            Channel channel = getChannel(player);

            channel.eventLoop().execute(() -> {
                if(channel.pipeline().get(ChannelListener.handle) != null) {
                    channel.pipeline().remove(ChannelListener.handle);
                }
            });
        });
    }

    private Channel getChannel(Player player) {
        return MinecraftReflection.getChannel(player);
    }

    @Override
    public Object onReceive(Player player, Object packet) {
        String type = packet.getClass().getSimpleName();

        Optional<PacketType.Client> optional = PacketType.Client.getPacket(type);

        if(!optional.isPresent()) {
            return packet;
        }

        PacketType.Client client = optional.get();
        NMSObject wrapper = client.wrappedPacket.apply(player, packet);

        Atlas.getInstance().getPacketProcessor().call(wrapper, client);
        boolean cancelled = false;

        return cancelled ? null : null;
    }

    @Override
    public Object onSend(Player player, Object packet) {
        String type = packet.getClass().getSimpleName();

        return cancelled ? null : null;
    }

    @RequiredArgsConstructor
    public class Listen extends ChannelDuplexHandler {
        final Player player;
        @Override
        public void channelRead(ChannelHandlerContext context, Object o) throws Exception {
            Object object = o;

            object = onReceive(player, object);

            if(object != null) {
                super.channelRead(context, object);
            }
        }

        @Override
        public void write(ChannelHandlerContext context, Object o, ChannelPromise promise) throws Exception {
            Object object = o;

            object = onSend(player, object);

            if(object != null) {
                super.write(context, object, promise);
            }
        }
    }
}
