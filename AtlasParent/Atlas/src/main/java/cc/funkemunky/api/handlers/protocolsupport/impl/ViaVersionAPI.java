package cc.funkemunky.api.handlers.protocolsupport.impl;

import cc.funkemunky.api.handlers.protocolsupport.Protocol;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;

public class ViaVersionAPI implements Protocol {

    @Override
    public int getPlayerVersion(Player player) {
        return Via.getAPI().getPlayerVersion(player.getUniqueId());
    }
}
