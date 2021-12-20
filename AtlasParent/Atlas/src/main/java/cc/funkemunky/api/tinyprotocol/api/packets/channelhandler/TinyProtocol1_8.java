/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

/*
 * The patches are licensed under MIT. Original credit goes to Kristian.
 */

package cc.funkemunky.api.tinyprotocol.api.packets.channelhandler;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.packets.AbstractTinyProtocol;
import cc.funkemunky.api.tinyprotocol.packet.login.WrappedHandshakingInSetProtocol;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumProtocol;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import cc.funkemunky.api.utils.RunUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import io.netty.channel.*;
import lombok.val;
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
public abstract class TinyProtocol1_8 implements AbstractTinyProtocol {
	private static final AtomicInteger ID = new AtomicInteger(0);

	// Looking up ServerConnection
	private static final WrappedClass packetClass = Reflections.getNMSClass("Packet");
	private static final WrappedClass playerConnection = Reflections.getNMSClass("PlayerConnection");
	private static final WrappedMethod methodSendPacket = playerConnection.getMethodByType(void.class,
			0, packetClass.getParent());

	// Packets we have to intercept
	private static final WrappedClass PACKET_SET_PROTOCOL = Reflections.getNMSClass("PacketHandshakingInSetProtocol");
	private static final WrappedClass PACKET_LOGIN_IN_START = Reflections.getNMSClass("PacketLoginInStart");
	private static final WrappedField getGameProfile = PACKET_LOGIN_IN_START.getFieldByType(GameProfile.class, 0),
			protocolId = PACKET_SET_PROTOCOL.getFieldByType(int.class, 0),
			protocolType = PACKET_SET_PROTOCOL.getFieldByType(Enum.class, 0);

	private List<ChannelFuture> gList;

	// Speedup channel lookup
	private Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();
	private Map<Channel, Integer> protocolLookup = new MapMaker().weakKeys().makeMap();
	private Listener listener;

	// Channels that have already been removed
	private Set<Channel> uninjectedChannels = Collections.newSetFromMap(new MapMaker().weakKeys().<Channel, Boolean>makeMap());

	// List of network markers
	private List<Object> networkManagers = new ArrayList<>();

	// Injected channel handlers
	private List<Channel> serverChannels = Lists.newArrayList();
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private ChannelInitializer<Channel> beginInitProtocol;
	private ChannelInitializer<Channel> endInitProtocol;

	// Current handler name
	private String handlerName;

	protected volatile boolean closed;
	protected Plugin plugin;
	private Object serverConnection;

	/**
	 * Construct a new instance of TinyProtocol, and start intercepting packets for all connected clients and future clients.
	 * <p>
	 * You can construct multiple instances per plugin.
	 *
	 * @param plugin - the plugin.
	 */
	public TinyProtocol1_8(final Plugin plugin) {
		this.plugin = plugin;

		// Compute handler name
		this.handlerName = getHandlerName();

		// Prepare existing players
		registerBukkitEvents();

		try {
			Bukkit.getLogger().info("Attempting to inject into netty");
			registerChannelHandler();

			RunUtils.taskLater(() -> registerPlayers(plugin), 2);
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

		/*Object ms = CraftReflection.getMinecraftServer();

		val scMethod = MinecraftReflection.minecraftServer
				.getMethodByType(MinecraftReflection.serverConnection.getParent(), 0);

		serverConnection = scMethod.invoke(ms);

		gList = MinecraftReflection.serverConnection.getFieldByType(List.class, 0).get(serverConnection);

		gList.forEach(future -> future.channel().pipeline().addLast(new MessageDecoder()));*/
	}

	private void createServerChannelHandler() {
		// Handle connected channels
		endInitProtocol = new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel channel) throws Exception {
				try {
					// Stop injecting channels
					if (!closed) {
						channel.eventLoop().submit(() -> injectChannelInternal(channel));
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
					try {
						injectPlayer(e.getPlayer());
					} catch (Exception ex) {

					}
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
		Object mcServer = CraftReflection.getMinecraftServer();
		Object serverConnection = MinecraftReflection.getServerConnection(mcServer);
		boolean looking = true;

		// We need to synchronize against this list
		for (Method m : mcServer.getClass().getMethods()) {
			if (m.getParameterTypes().length == 0 && m.getReturnType()
					.isAssignableFrom(MinecraftReflection.serverConnection.getParent())) {
				try {
					Object result = m.invoke(mcServer);
					if (result != null) serverConnection = result;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// We need to synchronize against this list
		createServerChannelHandler();

		// Find the correct list, or implicitly throw an exception
		for (int i = 0; looking; i++) {
			List<Object> list = Reflection.getField(serverConnection.getClass(), List.class, i).get(serverConnection);

			for (Object item : list) {
				//if (!ChannelFuture.class.isInstance(item))
				//	break;

				// Channel future that contains the server connection
				Channel serverChannel = ((ChannelFuture) item).channel();

				serverChannels.add(serverChannel);
				serverChannel.pipeline().addFirst(serverChannelHandler);
				Bukkit.getLogger().info("Server channel handler injected (" + serverChannel + ")");
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
	 * @param sender - the player that sent the packet, NULL for early login/status packets.
	 * @param packet - the packet being received.
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
		methodSendPacket.invoke(MinecraftReflection.getPlayerConnection(player), packet);
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
		return "atlas-protocol-handler";
	}

	/**
	 * Add a custom channel handler to the given player's channel pipeline, allowing us to intercept sent and received packets.
	 * <p>
	 * This will automatically be called when a player has logged in.
	 *
	 * @param player - the player to inject.
	 */
	public void injectPlayer(Player player) {
		Channel channel = getChannel(player);
		channelLookup.put(player.getName(), channel);
		injectChannelInternal(channel).player = player;
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
			channel = MinecraftReflection.getChannel(player);
			if (channel == null) return null;
			channelLookup.put(player.getName(), channel);
		}

		return channel;
	}

	public int getProtocolVersion(Player player) {
		Channel channel = channelLookup.get(player.getName());

		// Lookup channel again
		if (channel == null) {
			Object connection = MinecraftReflection.getPlayerConnection(CraftReflection.getEntityPlayer(player));
			Object manager = MinecraftReflection.getNetworkManager(connection);

			channelLookup.put(player.getName(), channel = MinecraftReflection.getChannel(manager));
		}

		Integer protocol = protocolLookup.get(channel);
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
		channelLookup.remove(player.getName());
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
		} else return;

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
		Channel ch = getChannel(player);
		if (ch == null) return false;
		return hasInjected(ch);
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

			// Remove our handlers
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				uninjectPlayer(player);
			}

			// Clean up Bukkit
			HandlerList.unregisterAll(listener);
			unregisterChannelHandler();
			closed = true;
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

			if (PACKET_LOGIN_IN_START.getParent().isInstance(msg)) {
				GameProfile profile = getGameProfile.get(msg);
				channelLookup.put(profile.getName(), channel);
			} else if (PACKET_SET_PROTOCOL.getParent().isInstance(msg)) {
				WrappedHandshakingInSetProtocol protocol = new WrappedHandshakingInSetProtocol(msg);
				if (protocol.enumProtocol == WrappedEnumProtocol.LOGIN) {
					protocolLookup.put(channel, protocol.a);
				}
			}

			if(player != null) {
				msg = onPacketInAsync(player, msg);
			} else {
				msg = onHandshake(ctx.channel().remoteAddress(), msg);
			}

			if (msg != null) {
				super.channelRead(ctx, msg);
			}
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

			if(player != null) {
				msg = onPacketOutAsync(player, msg);
			}

			if (msg != null) {
				super.write(ctx, msg, promise);
			}
		}
	}
}
