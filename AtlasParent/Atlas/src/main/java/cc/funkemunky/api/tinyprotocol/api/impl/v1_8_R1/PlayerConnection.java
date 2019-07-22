package cc.funkemunky.api.tinyprotocol.api.impl.v1_8_R1;

import cc.funkemunky.api.Atlas;
import net.minecraft.server.v1_8_R1.*;

public class PlayerConnection extends net.minecraft.server.v1_8_R1.PlayerConnection {
    public PlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }

    @Override
    public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {
        super.a(packetplayinsteervehicle);

        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinsteervehicle);
    }

    @Override
    public void a(PacketPlayInFlying packetplayinflying) {
        super.a(packetplayinflying);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinflying);
    }

    public void sendPacket(Packet packet, boolean fromHandler) {
        super.sendPacket(packet);

        if(!fromHandler) {
            Atlas.getInstance().getTinyProtocolHandler().onPacketOutAsync(getPlayer().getPlayer(), packet);
        }
    }
    @Override
    public void sendPacket(Packet packet) {
        super.sendPacket(packet);
        Atlas.getInstance().getTinyProtocolHandler().onPacketOutAsync(getPlayer().getPlayer(), packet);
    }

    @Override
    public void a(PacketPlayInBlockDig packetplayinblockdig) {
        super.a(packetplayinblockdig);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinblockdig);
    }

    @Override
    public void a(PacketPlayInBlockPlace packetplayinblockplace) {
        super.a(packetplayinblockplace);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinblockplace);
    }

    @Override
    public void a(PacketPlayInHeldItemSlot packetplayinhelditemslot) {
        super.a(packetplayinhelditemslot);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinhelditemslot);
    }

    @Override
    public void a(PacketPlayInChat packetplayinchat) {
        super.a(packetplayinchat);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinchat);
    }

    @Override
    public void a(PacketPlayInArmAnimation packetplayinarmanimation) {
        super.a(packetplayinarmanimation);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinarmanimation);
    }

    @Override
    public void a(PacketPlayInEntityAction packetplayinentityaction) {
        super.a(packetplayinentityaction);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinentityaction);
    }

    @Override
    public void a(PacketPlayInUseEntity packetplayinuseentity) {
        super.a(packetplayinuseentity);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinuseentity);
    }

    @Override
    public void a(PacketPlayInClientCommand packetplayinclientcommand) {
        super.a(packetplayinclientcommand);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinclientcommand);
    }

    @Override
    public void a(PacketPlayInCloseWindow packetplayinclosewindow) {
        super.a(packetplayinclosewindow);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinclosewindow);
    }

    @Override
    public void a(PacketPlayInWindowClick packetplayinwindowclick) {
        super.a(packetplayinwindowclick);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinwindowclick);
    }

    @Override
    public void a(PacketPlayInEnchantItem packetplayinenchantitem) {
        super.a(packetplayinenchantitem);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinenchantitem);
    }

    @Override
    public void a(PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {
        super.a(packetplayinsetcreativeslot);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinsetcreativeslot);
    }

    @Override
    public void a(PacketPlayInTransaction packetplayintransaction) {
        super.a(packetplayintransaction);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayintransaction);
    }

    @Override
    public void a(PacketPlayInUpdateSign packetplayinupdatesign) {
        super.a(packetplayinupdatesign);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinupdatesign);
    }

    @Override
    public void a(PacketPlayInKeepAlive packetplayinkeepalive) {
        super.a(packetplayinkeepalive);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinkeepalive);
    }

    @Override
    public void a(PacketPlayInAbilities packetplayinabilities) {
        super.a(packetplayinabilities);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinabilities);
    }

    @Override
    public void a(PacketPlayInTabComplete packetplayintabcomplete) {
        super.a(packetplayintabcomplete);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayintabcomplete);
    }

    @Override
    public void a(PacketPlayInSettings packetplayinsettings) {
        super.a(packetplayinsettings);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinsettings);
    }

    @Override
    public void a(PacketPlayInCustomPayload packetplayincustompayload) {
        super.a(packetplayincustompayload);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayincustompayload);
    }


    @Override
    public void a(PacketPlayInSpectate packetplayinspectate) {
        super.a(packetplayinspectate);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinspectate);
    }

    @Override
    public void a(PacketPlayInResourcePackStatus packetplayinresourcepackstatus) {
        super.a(packetplayinresourcepackstatus);
        Atlas.getInstance().getTinyProtocolHandler().onPacketInAsync(getPlayer().getPlayer(), packetplayinresourcepackstatus);
    }
}
