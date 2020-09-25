package cc.funkemunky.api.handlers.protocolsupport.impl;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.BungeeAPI;
import cc.funkemunky.api.handlers.protocolsupport.Protocol;
import cc.funkemunky.api.handlers.protocolsupport.ProtocolAPI;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import org.bukkit.entity.Player;

public class NoAPI implements Protocol {

    @Override
    public int getPlayerVersion(Player player) {
        if(Atlas.getInstance().getBungeeManager().isBungee()) {
            int version = TinyProtocolHandler.bungeeVersionCache
                    .computeIfAbsent(player.getUniqueId(), key -> BungeeAPI.getPlayerVersion(player));

            if(version != -1) return ProtocolVersion.getVersion(version).getVersion();
        }
        return TinyProtocolHandler.getInstance().getProtocolVersion(player);
    }
}
