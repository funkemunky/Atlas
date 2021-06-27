package cc.funkemunky.api.packet.channel;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ChannelNew implements ChannelListener {

    private final Map<String, Channel> channelCache = new WeakHashMap<>();
    private final Map<Channel, Integer> versionCache = new HashMap<>();

    private static String handle = "atlas_packet_listener";

    /* private static final FieldAccessor<Integer> protocolId = Reflection.getField(PACKET_SET_PROTOCOL, int.class, 0);
	private static final FieldAccessor<Enum> protocolType = Reflection.getField(PACKET_SET_PROTOCOL, Enum.class, 0); */

    public ChannelNew() {
        System.out.println("Running executor for server registering...");
        System.out.println("Running registration...");
    }

    @Override
    public int getProtocolVersion(Player player) {
        Channel channel = getChannel(player);

        return versionCache.getOrDefault(channel, -1);
    }

    @Override
    public void inject(Player player) {
        if(!Atlas.getInstance().isEnabled()) return;
        Channel channel = getChannel(player);

        if(channel == null) return;

        channel.eventLoop().execute(() -> {
            Incoming listen = (Incoming) channel.pipeline().get(handle);

            if(listen == null) {
                listen = new Incoming(player);

                if(channel.pipeline().get(handle) != null) {
                    channel.pipeline().remove(handle);
                }
                channel.pipeline().addBefore("packet_handler", handle, listen);
            }
        });
    }

    public void inject(Channel channel) {
        if(!Atlas.getInstance().isEnabled()) return;

        channel.eventLoop().execute(() -> {
            Incoming listen = (Incoming) channel.pipeline().get(handle);

            if(listen == null) {
                listen = new Incoming(null);

                if(channel.pipeline().get(handle) != null) {
                    channel.pipeline().remove(handle);
                }
                channel.pipeline().addBefore("packet_handler", handle, listen);
            }
        });
    }

    @Override
    public void uninject(Player player) {
        Channel channel = getChannel(player);

        uninject(channel);

        channelCache.remove(player.getName());
        versionCache.remove(channel);
    }

    public void uninject(Channel channel) {
        channel.eventLoop().execute(() -> {
            if(channel.pipeline().get(handle) != null) {
                channel.pipeline().remove(handle);
            }
        });
    }

    @Override
    public void sendPacket(Player player, Object packet) {
        getChannel(player).pipeline().writeAndFlush(packet);
    }

    @Override
    public void receivePacket(Player player, Object packet) {
        getChannel(player).pipeline().context("encoder").fireChannelRead(packet);
    }

    private Channel getChannel(Player player) {
        return channelCache.compute(player.getName(), (key, channel) -> {
           if(channel == null) {
               return MinecraftReflection.getChannel(player);
           }
           return channel;
        });
    }

    public Object onReceive(Player player, Object packet) {
        return packet;
    }

    public Object onSend(Player player, Object packet) {
        return packet;
    }

    public Object onHandshake(SocketAddress address, Object packet) {
        return packet;
    }

    @RequiredArgsConstructor
    public class Incoming extends ByteToMessageDecoder {
        final Player player;

        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
                throws Exception {

        }
    }
}
