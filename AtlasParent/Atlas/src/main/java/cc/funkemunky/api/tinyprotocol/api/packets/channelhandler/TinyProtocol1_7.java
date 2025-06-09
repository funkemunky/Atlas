package cc.funkemunky.api.tinyprotocol.api.packets.channelhandler;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.api.packets.AbstractTinyProtocol;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.MethodInvoker;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import com.google.common.collect.MapMaker;
import lombok.val;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.io.netty.channel.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Represents a very tiny alternative to ProtocolLib.
 * <p>
 * It now supports intercepting packets during login and status ping (such as OUT_SERVER_PING)!
 *
 * @author Kristian
 */

public abstract class TinyProtocol1_7 implements AbstractTinyProtocol {
    private static final AtomicInteger ID = new AtomicInteger(0);

    // Used in order to lookup a channel
    private static final MethodInvoker getPlayerHandle = Reflection.getMethod("{obc}.entity.CraftPlayer", "getHandle");
    private static final FieldAccessor<Object> getConnection = Reflection.getField("{nms}.EntityPlayer", "playerConnection", Object.class);
    private static final FieldAccessor<Object> getManager = Reflection.getField("{nms}.PlayerConnection", "networkManager", Object.class);
    private static final FieldAccessor<Channel> getChannel = Reflection.getField("{nms}.NetworkManager", Channel.class, 0);

    // Looking up ServerConnection
    private static final Class<Object> minecraftServerClass = Reflection.getUntypedClass("{nms}.MinecraftServer");
    private static final Class<Object> serverConnectionClass = Reflection.getUntypedClass("{nms}.ServerConnection");
    private static final FieldAccessor<Object> getMinecraftServer = Reflection.getField("{obc}.CraftServer", minecraftServerClass, 0);
    private static final FieldAccessor<Object> getServerConnection = Reflection.getField(minecraftServerClass, serverConnectionClass, 0);
    private static final MethodInvoker getNetworkMarkers = Reflection.getTypedMethod(serverConnectionClass, 0, List.class, serverConnectionClass);

    // Packets we have to intercept
    private static final Class<?> PACKET_SET_PROTOCOL = Reflection.getMinecraftClass("PacketHandshakingInSetProtocol");
    private static final Class<?> PACKET_LOGIN_IN_START = Reflection.getMinecraftClass("PacketLoginInStart");
    private static final FieldAccessor<GameProfile> getGameProfile = Reflection.getField(PACKET_LOGIN_IN_START, GameProfile.class, 0);
    private static final FieldAccessor<Integer> protocolId = Reflection.getField(PACKET_SET_PROTOCOL, int.class, 0);
    private static final FieldAccessor<Enum> protocolType = Reflection.getField(PACKET_SET_PROTOCOL, Enum.class, 0);


    // Speedup channel lookup
    private Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();
    private Map<Channel, Integer> protocolLookup = new MapMaker().weakKeys().makeMap();
    private Listener listener;

    // Channels that have already been removed
    private Set<Channel> uninjectedChannels = Collections.newSetFromMap(new MapMaker().weakKeys().<Channel, Boolean>makeMap());

    // List of network markers
    private List<Object> networkManagers;

    // Injected channel handlers
    private List<Channel> serverChannels = new ArrayList<>();
    private ChannelInboundHandlerAdapter serverChannelHandler;
    private ChannelInitializer<Channel> beginInitProtocol;
    private ChannelInitializer<Channel> endInitProtocol;

    // Current handler name
    private String handlerName;

    protected volatile boolean closed;
    protected Plugin plugin;

    /**
     * Construct a new instance of TinyProtocol, and start intercepting packets for all connected clients and future clients.
     * <p>
     * You can construct multiple instances per plugin.
     *
     * @param plugin - the plugin.
     */
    public TinyProtocol1_7(final Plugin plugin) {
        this.plugin = plugin;

        // Compute handler name
        this.handlerName = getHandlerName();

        // Prepare existing players
        registerBukkitEvents();

        try {
            Bukkit.getLogger().info("Attempting to inject into netty");
            registerChannelHandler();
            registerPlayers(plugin);
        } catch (IllegalArgumentException ex) {
            // Damn you, late bind
            plugin.getLogger().info("Attempting to delay injection.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    registerChannelHandler();
                    registerPlayers(plugin);
                    plugin.getLogger().info("Injection complete.");
                }
            }.runTask(plugin);
        }
    }

    private void createServerChannelHandler() {
        // Handle connected channels
        endInitProtocol = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                try {
                    // This can take a while, so we need to stop the main thread from interfering
                    synchronized (networkManagers) {
                        // Stop injecting channels
                        if (!closed) {
                            channel.eventLoop().submit(() -> injectChannelInternal(channel));
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
                }
            }

        };

        // This is executed before Minecraft's channel handler
        beginInitProtocol = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast(endInitProtocol);
            }

        };

        serverChannelHandler = new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                Channel channel = (Channel) msg;

                channel.pipeline().addFirst(beginInitProtocol);
                ctx.fireChannelRead(msg);
            }

        };
    }

    /**
     * Register bukkit events.
     */
    private void registerBukkitEvents() {
        listener = new Listener() {

            @EventHandler(priority = EventPriority.LOWEST)
            public final void onPlayerLogin(PlayerJoinEvent e) {
                if (closed)
                    return;

                Channel channel = getChannel(e.getPlayer());

                // Don't inject players that have been explicitly uninjected
                if (!uninjectedChannels.contains(channel)) {
                    Bukkit.getScheduler().runTaskLater(Atlas.getInstance(), () -> injectPlayer(e.getPlayer()), 1L); //We delay it on the main thread since servers do occasionally lag.
                }
            }

            @EventHandler
            public final void onPluginDisable(PluginDisableEvent e) {
                if (e.getPlugin().equals(plugin)) {
                    close();
                }
            }

        };

        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @SuppressWarnings("unchecked")
    private void registerChannelHandler() {
        Object mcServer = getMinecraftServer.get(Bukkit.getServer());
        Object serverConnection = getServerConnection.get(mcServer);
        boolean looking = true;

        // We need to synchronize against this list
        networkManagers = (List) getNetworkMarkers.invoke(null, serverConnection);
        createServerChannelHandler();

        // Find the correct list, or implicitly throw an exception
        for (int i = 0; looking; i++) {
            List<Object> list = Reflection.getField(serverConnection.getClass(), List.class, i).get(serverConnection);

            for (Object item : list) {
                //if (!ChannelFuture.class.isInstance(item))
                //	break;

                // Channel future that contains the server connection
                Channel serverChannel = ((ChannelFuture) item).channel();

                serverChannels.add(serverChannel);;
                serverChannel.pipeline().addFirst(serverChannelHandler);
                Bukkit.getLogger().severe("Server channel handler injected (" + serverChannel + ")");
                looking = false;
            }
        }
    }

    private void unregisterChannelHandler() {
        if (serverChannelHandler == null)
            return;

        for (Channel serverChannel : serverChannels) {
            final ChannelPipeline pipeline = serverChannel.pipeline();

            // Remove channel handler
            serverChannel.eventLoop().execute(() -> {
                try {
                    pipeline.remove(serverChannelHandler);
                } catch (NoSuchElementException e) {
                    // That's fine
                }
            });
        }
    }

    private void registerPlayers(Plugin plugin) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            injectPlayer(player);
        }
    }

    /**
     * Invoked when the server is starting to send a packet to a player.
     * <p>
     * Note that this is not executed on the main thread.
     *
     * @param receiver - the receiving player, NULL for early login/status packets.
     * @param packet   - the packet being sent.
     * @return The packet to send instead, or NULL to cancel the transmission.
     */
    public Object onPacketOutAsync(Player receiver, Object packet) {
        return packet;
    }

    /**
     * Invoked when the server has received a packet from a given player.
     * <p>
     * Use {@link Channel#remoteAddress()} to get the remote address of the client.
     *
     * @param sender  - the player that sent the packet, NULL for early login/status packets.
     * @param packet  - the packet being received.
     * @return The packet to recieve instead, or NULL to cancel.
     */
    public Object onPacketInAsync(Player sender, Object packet) {
        return packet;
    }

    public Object onHandshake(SocketAddress address, Object packet) {
        return packet;
    }

    /**
     * Send a packet to a particular player.
     * <p>
     * Note that {@link #onPacketOutAsync(Player, Object)} will be invoked with this packet.
     *
     * @param player - the destination player.
     * @param packet - the packet to send.
     */
    public void sendPacket(Player player, Object packet) {
        sendPacket(getChannel(player), packet);
    }

    /**
     * Send a packet to a particular client.
     * <p>
     * Note that {@link #onPacketOutAsync(Player, Object)} will be invoked with this packet.
     *
     * @param channel - client identified by a channel.
     * @param packet  - the packet to send.
     */
    public void sendPacket(Channel channel, Object packet) {
        channel.pipeline().writeAndFlush(packet);
    }

    /**
     * Pretend that a given packet has been received from a player.
     * <p>
     * Note that {@link #onPacketInAsync(Player, Object)} will be invoked with this packet.
     *
     * @param player - the player that sent the packet.
     * @param packet - the packet that will be received by the server.
     */
    public void receivePacket(Player player, Object packet) {
        receivePacket(getChannel(player), packet);
    }

    /**
     * Pretend that a given packet has been received from a given client.
     * <p>
     * Note that {@link #onPacketInAsync(Player, Object)} will be invoked with this packet.
     *
     * @param channel - client identified by a channel.
     * @param packet  - the packet that will be received by the server.
     */
    public void receivePacket(Channel channel, Object packet) {
        channel.pipeline().context("encoder").fireChannelRead(packet);
    }

    /**
     * Retrieve the name of the channel injector, default implementation is "tiny-" + plugin name + "-" + a unique ID.
     * <p>
     * Note that this method will only be invoked once. It is no longer necessary to override this to support multiple instances.
     *
     * @return A unique channel handler name.
     */
    protected String getHandlerName() {
        return "tiny-" + plugin.getName() + "-" + ID.incrementAndGet();
    }

    /**
     * Add a custom channel handler to the given player's channel pipeline, allowing us to intercept sent and received packets.
     * <p>
     * This will automatically be called when a player has logged in.
     *
     * @param player - the player to inject.
     */
    public void injectPlayer(Player player) {
        injectChannelInternal(getChannel(player)).player = player;
    }

    /**
     * Add a custom channel handler to the given channel.
     *
     * @param channel - the channel to inject.
     * @return The intercepted channel, or NULL if it has already been injected.
     */
    public void injectChannel(Channel channel) {
        injectChannelInternal(channel);
    }

    /**
     * Add a custom channel handler to the given channel.
     *
     * @param channel - the channel to inject.
     * @return The packet interceptor.
     */
    private PacketInterceptor injectChannelInternal(Channel channel) {
        try {
            PacketInterceptor interceptor = (PacketInterceptor) channel.pipeline().get(handlerName);

            // Inject our packet interceptor
            if (interceptor == null) {
                interceptor = new PacketInterceptor();
                val context = channel.pipeline().context("packet_handler");
                if(context != null) {
                    channel.pipeline().addBefore("packet_handler", handlerName, interceptor);
                    uninjectedChannels.remove(channel);
                } else {
                    uninjectedChannels.add(channel);
                }
            }

            return interceptor;
        } catch (IllegalArgumentException e) {
            // Try again
            return (PacketInterceptor) channel.pipeline().get(handlerName);
        }
    }

    /**
     * Retrieve the Netty channel associated with a player. This is cached.
     *
     * @param player - the player.
     * @return The Netty channel.
     */
    public Channel getChannel(Player player) {
        Channel channel = channelLookup.get(player.getName());

        // Lookup channel again
        if (channel == null) {
            Object connection = getConnection.get(getPlayerHandle.invoke(player));
            Object manager = getManager.get(connection);

            channelLookup.put(player.getName(), channel = getChannel.get(manager));
        }

        return channel;
    }

    public int getProtocolVersion(Player player) {
       Channel channel = channelLookup.get(player.getName());

		// Lookup channel again
		if (channel == null) {
			Object connection = getConnection.get(getPlayerHandle.invoke(player));
			Object manager = getManager.get(connection);

			channelLookup.put(player.getName(), channel = getChannel.get(manager));
		}

		Integer protocol = protocolLookup.get(channel);

		int protocolVersion;
		try {
			Class<?> Via = Class.forName("us.myles.ViaVersion.api.Via");
			Class<?> clazzViaAPI = Class.forName("us.myles.ViaVersion.api.ViaAPI");
			Object ViaAPI = Via.getMethod("getAPI").invoke(null);
			Method getPlayerVersion = clazzViaAPI.getMethod("getPlayerVersion", Object.class);
			protocolVersion = (int) getPlayerVersion.invoke(ViaAPI, player);
			protocolLookup.put(channel, protocolVersion);
			return protocolVersion;
		} catch (Throwable e) {

		}
		if (protocol != null) {
			return protocol;
		} else return -1;
    }

    /**
     * Uninject a specific player.
     *
     * @param player - the injected player.
     */
    public void uninjectPlayer(Player player) {
        uninjectChannel(getChannel(player));
    }

    /**
     * Uninject a specific channel.
     * <p>
     * This will also disable the automatic channel injection that occurs when a player has properly logged in.
     *
     * @param channel - the injected channel.
     */
    public void uninjectChannel(final Channel channel) {
        // No need to guard against this if we're closing
        if (!closed) {
            uninjectedChannels.add(channel);
        }

        // See ChannelInjector in ProtocolLib, line 590
        channel.eventLoop().execute(() -> channel.pipeline().remove(handlerName));
    }

    /**
     * Determine if the given player has been injected by TinyProtocol.
     *
     * @param player - the player.
     * @return TRUE if it is, FALSE otherwise.
     */
    public boolean hasInjected(Player player) {
        return hasInjected(getChannel(player));
    }

    /**
     * Determine if the given channel has been injected by TinyProtocol.
     *
     * @param channel - the channel.
     * @return TRUE if it is, FALSE otherwise.
     */
    public boolean hasInjected(Channel channel) {
        return channel.pipeline().get(handlerName) != null;
    }

    /**
     * Cease listening for packets. This is called automatically when your plugin is disabled.
     */
    public final void close() {
        if (!closed) {
            closed = true;

            // Remove our handlers
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                uninjectPlayer(player);
            }

            // Clean up Bukkit
            HandlerList.unregisterAll(listener);
            unregisterChannelHandler();
        }
    }

    /**
     * Channel handler that is inserted into the player's channel pipeline, allowing us to intercept sent and received packets.
     *
     * @author Kristian
     */
    private final class PacketInterceptor extends ChannelDuplexHandler {
        // Updated by the login event
        public Player player;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // Intercept channel
            final Channel channel = ctx.channel();
            if (PACKET_LOGIN_IN_START.isInstance(msg)) {
                GameProfile profile = getGameProfile.get(msg);
                channelLookup.put(profile.getName(), channel);
            } else if (PACKET_SET_PROTOCOL.isInstance(msg)) {
                String protocol = protocolType.get(msg).name();
                if (protocol.equalsIgnoreCase("LOGIN")) {
                    protocolLookup.put(channel, protocolId.get(msg));
                }
            }

            if(player != null) {
                try {
                    msg = onPacketInAsync(player, msg);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Error in onPacketInAsync().", e);
                }
            } else msg = onHandshake(ctx.channel().remoteAddress(), msg);

            if (msg != null) {
                super.channelRead(ctx, msg);
            }
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if(player != null) {
                try {
                    msg = onPacketOutAsync(player, msg);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Error in onPacketOutAsync().", e);
                }
            }

            if (msg != null) {
                super.write(ctx, msg, promise);
            }
        }
    }
}