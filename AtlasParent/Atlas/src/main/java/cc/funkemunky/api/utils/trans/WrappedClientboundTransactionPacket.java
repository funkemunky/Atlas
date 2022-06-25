package cc.funkemunky.api.utils.trans;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
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

    public WrappedClientboundTransactionPacket(PacketSendEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            WrapperPlayServerWindowConfirmation wrapper = new WrapperPlayServerWindowConfirmation(event);

            this.packet = wrapper;

            this.actionId = wrapper.getActionId();
            this.window = wrapper.getWindowId();
            this.accept = wrapper.isAccepted();
        } else if(event.getPacketType() == PacketType.Play.Server.PING) {
            WrapperPlayServerPing wrapper = new WrapperPlayServerPing(event);

            this.packet = wrapper;

            this.actionId = (short) (wrapper.getId() & 0xFFFF);
            this.window = (wrapper.getId() >> 16) & 0xFF;
            this.accept = (wrapper.getId() & (1 << 30)) != 0;
        } else {
            throw new IllegalArgumentException(
                    "Packet is not a WrapperPlayServerWindowConfirmation or WrapperPlayServerPing");
        }
    }
}
