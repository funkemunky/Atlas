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
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.Reflections;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import cc.funkemunky.api.utils.ReflectionsUtil;
import lombok.val;
import net.minecraft.util.com.google.common.collect.MapMaker;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class ChannelHandler1_7 extends ChannelHandlerAbstract {
    @Override public void addChannel(Player player) {
        net.minecraft.util.io.netty.channel.Channel channel = getChannel(player);
        this.addChannelHandlerExecutor.execute(() -> {
            if (channel != null && channel.pipeline().get(this.playerKey) == null) {
                channel.pipeline().addBefore(this.handlerKey, this.playerKey, new ChannelHandler(player, this));
            }
        });
    }

    @Override public void removeChannel(Player player) {
        net.minecraft.util.io.netty.channel.Channel channel = getChannel(player);
        this.removeChannelHandlerExecutor.execute(() -> {
            if (channel != null && channel.pipeline().get(this.playerKey) != null) {
                channel.pipeline().remove(this.playerKey);
            }
        });
    }

    private net.minecraft.util.io.netty.channel.Channel getChannel(Player player) {
        return (net.minecraft.util.io.netty.channel.Channel) Reflections.getNMSClass("NetworkManager").getFirstFieldByType(net.minecraft.util.io.netty.channel.Channel.class).get(networkManagerField.get(playerConnectionField.get(ReflectionsUtil.getEntityPlayer(player))));
    }

    private static class ChannelHandler extends net.minecraft.util.io.netty.channel.ChannelDuplexHandler {
        private final Player player;
        private final ChannelHandlerAbstract channelHandlerAbstract;

        ChannelHandler(Player player, ChannelHandlerAbstract channelHandlerAbstract) {
            this.player = player;
            this.channelHandlerAbstract = channelHandlerAbstract;
        }

        @Override public void write(net.minecraft.util.io.netty.channel.ChannelHandlerContext ctx, Object msg, net.minecraft.util.io.netty.channel.ChannelPromise promise) throws Exception {
            Object packet = Atlas.getInstance().getTinyProtocolHandler().onPacketOutAsync(player, msg);
            if (packet != null) {
                super.write(ctx, packet, promise);
            }
        }

        @Override public void channelRead(net.minecraft.util.io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
            Object packet = Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(player, msg);
            if (packet != null) {
                super.channelRead(ctx, packet);
            }
        }
    }

    public void sendPacket(Player player, Object packet) {
        getChannel(player).pipeline().writeAndFlush(packet);
    }
}
