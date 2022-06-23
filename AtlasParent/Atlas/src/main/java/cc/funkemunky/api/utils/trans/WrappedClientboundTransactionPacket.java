package cc.funkemunky.api.utils.trans;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedClientboundTransactionPacket {

    private PacketWrapper<?> packet;
    private final short actionId;
    private final int window;
    private final boolean accept;

    public WrappedClientboundTransactionPacket(Player player, short actionId, int window, boolean accept) {
        ClientVersion clientVersion = PacketEvents.getAPI().getPlayerManager().getClientVersion(player);

        this.actionId = actionId;
        this.window = window;
        this.accept = accept;

        if(clientVersion.isOlderThan(ClientVersion.V_1_17)) {
            packet = new WrapperPlayServerWindowConfirmation(window, actionId, accept);
        } else {
            packet = new WrapperPlayServerPing((int)((accept ? 1 : 0) << 30) | (window << 16) | (actionId & 0xFFFF));
        }
    }

    public WrappedClientboundTransactionPacket(PacketWrapper<?> packet) {
        if(packet instanceof WrapperPlayServerWindowConfirmation) {
            this.packet = packet;

            this.actionId = ((WrapperPlayServerWindowConfirmation) packet).getActionId();
            this.window = ((WrapperPlayServerWindowConfirmation) packet).getWindowId();
            this.accept = ((WrapperPlayServerWindowConfirmation) packet).isAccepted();
        } else if(packet instanceof WrapperPlayServerPing) {
            WrapperPlayServerPing pingPacket = (WrapperPlayServerPing) packet;

            this.actionId = (short) (pingPacket.getId() & 0xFFFF);
            this.window = (pingPacket.getId() >> 16) & 0xFF;
            this.accept = (pingPacket.getId() & (1 << 30)) != 0;
        } else {
            throw new IllegalArgumentException(
                    "Packet is not a WrapperPlayServerWindowConfirmation or WrapperPlayServerPing");
        }
        this.packet = packet;
    }
}
