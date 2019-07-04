/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package cc.funkemunky.api.tinyprotocol.api.packets;

import cc.funkemunky.api.tinyprotocol.api.packets.channelhandler.ChannelHandler1_7;
import cc.funkemunky.api.tinyprotocol.api.packets.channelhandler.ChannelHandler1_8;
import cc.funkemunky.api.tinyprotocol.api.packets.channelhandler.ChannelHandlerAbstract;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.Reflections;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
//@Init
public class ChannelInjector implements Listener {
    private ChannelHandlerAbstract channel;

    public ChannelInjector() {
        this.channel = Reflections.classExists("io.netty.channel.Channel") ? new ChannelHandler1_8() : new ChannelHandler1_7();
    }

    public void addChannel(Player player) {
        this.channel.addChannel(player);
    }

    public void removeChannel(Player player) {
        this.channel.removeChannel(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        addChannel(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeChannel(event.getPlayer());
    }
}
