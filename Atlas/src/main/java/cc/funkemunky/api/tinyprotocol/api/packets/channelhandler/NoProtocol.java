package cc.funkemunky.api.tinyprotocol.api.packets.channelhandler;

import cc.funkemunky.api.tinyprotocol.api.packets.AbstractTinyProtocol;
import org.bukkit.entity.Player;

public class NoProtocol implements AbstractTinyProtocol {
    @Override
    public void sendPacket(Player player, Object packet) {

    }

    @Override
    public void receivePacket(Player player, Object packet) {

    }

    @Override
    public void injectPlayer(Player player) {

    }

    @Override
    public int getProtocolVersion(Player player) {
        return 0;
    }

    @Override
    public void uninjectPlayer(Player player) {

    }

    @Override
    public boolean hasInjected(Player player) {
        return false;
    }

    @Override
    public void close() {

    }
}
