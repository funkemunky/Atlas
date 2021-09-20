/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.api.packets;

import org.bukkit.entity.Player;

public interface AbstractTinyProtocol {
    void sendPacket(Player player, Object packet);

    void receivePacket(Player player, Object packet);

    void injectPlayer(Player player);

    int getProtocolVersion(Player player);

    void uninjectPlayer(Player player);

    boolean hasInjected(Player player);

    void close();
}
