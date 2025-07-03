package cc.funkemunky.api.handlers.protocolsupport.impl;

import cc.funkemunky.api.handlers.protocolsupport.Protocol;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

public class ProtocolSupport implements Protocol {

    @Override
    public int getPlayerVersion(Player player) {
        return ProtocolSupportAPI.getProtocolVersion(player).getId();
    }
}
