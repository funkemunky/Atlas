/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.api;

import org.bukkit.entity.Player;

public interface AbstractPackets {
    void sendPacket(Player player, Object packet);

    void close();
}
