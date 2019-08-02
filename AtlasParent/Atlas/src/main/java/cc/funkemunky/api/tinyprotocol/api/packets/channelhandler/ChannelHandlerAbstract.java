/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package cc.funkemunky.api.tinyprotocol.api.packets.channelhandler;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.Reflections;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.types.WrappedField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class ChannelHandlerAbstract {
    static final WrappedField networkManagerField = Reflections.getNMSClass("PlayerConnection").getFieldByName("networkManager");
    static final WrappedField playerConnectionField = Reflections.getNMSClass("EntityPlayer").getFieldByName("playerConnection");
    final Executor addChannelHandlerExecutor;
    final Executor removeChannelHandlerExecutor;
    final String handlerKey;
    final String playerKey;

    ChannelHandlerAbstract() {
        this.addChannelHandlerExecutor = Executors.newSingleThreadExecutor();
        this.removeChannelHandlerExecutor = Executors.newSingleThreadExecutor();
        this.handlerKey = "packet_handler";
        this.playerKey = "atlas_player_handler";
    }

    public Object run(Player player, Object packet) {
        if (!Atlas.getInstance().isDone()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Atlas");
            if (plugin != null && plugin.isEnabled()) {
                Object channelInjector = Reflections.getClass(plugin.getClass()).getMethod("getChannelInjector").invoke(plugin);
                Reflections.getClass(channelInjector.getClass()).getMethod("addChannel", Player.class).invoke(channelInjector, player);
            }
            return true;
        }
        if (packet != null && player != null && player.isOnline()) {
            String name = packet.getClass().getSimpleName().replaceAll("PacketPlayInUseItem", "PacketPlayInBlockPlace")
                    .replaceAll(Packet.Client.LEGACY_LOOK, Packet.Client.LOOK)
                    .replaceAll(Packet.Client.LEGACY_POSITION, Packet.Client.POSITION)
                    .replaceAll(Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.POSITION_LOOK);;

            if(name.contains("PacketPlayIn")) {
                PacketReceiveEvent event = new PacketReceiveEvent(player, packet, name);

                Atlas.getInstance().getEventManager().callEvent(event);

                return event.isCancelled() ? null : event.getPacket();
            } else if(name.contains("PacketPlayOut")) {
                PacketSendEvent event = new PacketSendEvent(player, packet, name);

                Atlas.getInstance().getEventManager().callEvent(event);

                return event.isCancelled() ? null : event.getPacket();
            }
        }
        return packet;
    }

    public abstract void addChannel(Player player);

    public abstract void removeChannel(Player player);

    public abstract void sendPacket(Player player, Object packet);
}
