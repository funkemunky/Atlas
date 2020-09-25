package cc.funkemunky.api.tinyprotocol.api.channel;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.utils.MiscUtils;
import net.minecraft.util.io.netty.channel.ChannelFuture;
import net.minecraft.util.io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelInitializer;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ChannelOld extends ChannelListener {

    private static final WrappedClass classPacketSetProtocol
            = Reflections.getNMSClass("PacketHandshakingInSetProtocol");

    private final Map<Channel, Integer> versionCache = new HashMap<>();
    private final Map<Player, Channel> channelCache = new WeakHashMap<>();
    
    private static WrappedField fieldFutureList = MinecraftReflection.serverConnection
            .getFieldByType(List.class, 0);
    private static final WrappedField fieldProtocolId = classPacketSetProtocol.getFieldByType(int.class, 0),
            fieldProtocolType = classPacketSetProtocol.getFieldByType(Enum.class, 0);

    private ChannelInboundHandlerAdapter serverRegisterHandler;
    private ChannelInitializer<Channel> hackyRegister, channelRegister;

    public ChannelOld() {
        List<ChannelFuture> futures = fieldFutureList.get(MinecraftReflection.getServerConnection());

        channelRegister = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) {
                Atlas.getInstance().getService().execute(() -> inject(channel));
            }

        };

        hackyRegister = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast(channelRegister);
            }
        };

        serverRegisterHandler = new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg)  {
                Channel channel = (Channel) msg;
                channel.pipeline().addFirst(hackyRegister);
                ctx.fireChannelRead(msg);
            }
        };

        new BukkitRunnable() {
            @Override
            public void run() {
                futures.forEach(future -> {
                    Channel channel = future.channel();

                    channel.pipeline().addFirst(serverRegisterHandler);

                    MiscUtils.printToConsole("Injected server channel " + channel.toString());

                    ChannelListener.registered = true;
                });
            }
        }.runTask(Atlas.getInstance());
    }

    @Override
    public int getProtocolVersion(Player player) {
        Channel channel = getChannel(player);

        return versionCache.getOrDefault(channel, -1);
    }

    @Override
    public void inject(Player player) {
        if(serverStopped) return;
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

    public void inject(Channel channel) {
        if(serverStopped) return;
        Listen listen = (Listen) channel.pipeline().get(ChannelListener.handle);

        if(listen == null) {
            listen = new Listen(null);

            if(channel.pipeline().get(ChannelListener.handle) != null) {
                channel.pipeline().remove(ChannelListener.handle);
            }
            channel.pipeline().addBefore("packet_handler", ChannelListener.handle, listen);
        }
    }

    @Override
    public void uninject(Player player) {
        Channel channel = getChannel(player);

        unject(channel);
    }

    public void unject(Channel channel) {
        channel.eventLoop().execute(() -> {
            if(channel.pipeline().get(ChannelListener.handle) != null) {
                channel.pipeline().remove(ChannelListener.handle);
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
        return channelCache.compute(player, (key, channel) -> {
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
    public class Listen extends ChannelDuplexHandler {
        final Player player;
        @Override
        public void channelRead(ChannelHandlerContext context, Object o) throws Exception {
            Object object = o;

            if(player != null) {
                object = onReceive(player, object);
            } else object = onHandshake(context.channel().remoteAddress(), o);object = onReceive(player, object);

            if (classPacketSetProtocol.getParent().isInstance(o)) {
                String protocol = ((Enum)fieldProtocolType.get(o)).name();
                if (protocol.equalsIgnoreCase("LOGIN")) {
                    int id = fieldProtocolId.get(o);
                    versionCache.put(context.channel(), id);
                }
            }

            if(object != null) {
                super.channelRead(context, object);
            }
        }

        @Override
        public void write(ChannelHandlerContext context, Object o, ChannelPromise promise) throws Exception {
            Object object = o;

            if(player != null) {
                object = onSend(player, object);
            }  else object = onHandshake(context.channel().remoteAddress(), o);

            if(object != null) {
                super.write(context, object, promise);
            }
        }
    }
}
