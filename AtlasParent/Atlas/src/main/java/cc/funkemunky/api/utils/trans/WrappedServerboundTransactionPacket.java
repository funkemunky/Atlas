package cc.funkemunky.api.utils.trans;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedServerboundTransactionPacket {

    private PacketWrapper<?> packet;
    private final short actionId;
    private final int window;
    private final boolean accept;

    public WrappedServerboundTransactionPacket(Player player, short actionId, int window, boolean accept) {
        this.actionId = actionId;
        this.window = window;
        this.accept = accept;

        ClientVersion clientVersion = PacketEvents.getAPI().getPlayerManager().getClientVersion(player);

        if(clientVersion.isOlderThan(ClientVersion.V_1_17)) {
            packet = new WrapperPlayClientWindowConfirmation(window, actionId, accept);
        } else {
            packet = new WrapperPlayClientPong((int)((accept ? 1 : 0) << 30) | (window << 16) | (actionId & 0xFFFF));
        }
    }

    public WrappedServerboundTransactionPacket(PacketWrapper<?> packet) {
        if (packet instanceof WrapperPlayClientWindowConfirmation) {
            this.packet = packet;

            this.actionId = ((WrapperPlayClientWindowConfirmation) packet).getActionId();
            this.window = ((WrapperPlayClientWindowConfirmation) packet).getWindowId();
            this.accept = ((WrapperPlayClientWindowConfirmation) packet).isAccepted();
        } else if (packet instanceof WrapperPlayClientPong) {
            WrapperPlayClientPong pongPacket = (WrapperPlayClientPong) packet;

            this.packet = pongPacket;

            this.actionId = (short) (pongPacket.getId() & 0xFFFF);
            this.window = (pongPacket.getId() >> 16) & 0xFF;
            this.accept = (pongPacket.getId() & (1 << 30)) != 0;
        } else {
            throw new IllegalArgumentException(
                    "Packet is not a WrapperPlayClientWindowConfirmation or WrapperPlayClientPong");
        }
    }
}
