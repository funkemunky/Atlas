package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import cc.funkemunky.api.utils.ReflectionsUtil;
import lombok.Getter;
import lombok.val;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class TinyProtocolHandler {

    public static boolean enabled = true;
    private static Class<?> customConnection;

    public TinyProtocolHandler() {
        customConnection = Reflection.getClass("cc.funkemunky.api.tinyprotocol.api.impl." + ProtocolVersion.getGameVersion().getServerVersion() + ".PlayerConnection");
    }

    // Purely for making the code cleaner
    public static void sendPacket(Player player, Object packet) {
        Object playerConnection = getPlayerConnection(player);

        Reflection.getMethod(customConnection, "sendPacket", ReflectionsUtil.packet, boolean.class).invoke(playerConnection, packet, false);
    }

    private boolean didPosition = false;

    public void onPacketOutAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);

        PacketSendEvent event = new PacketSendEvent(sender, packet, packetName);

        //EventManager.callEvent(new cc.funkemunky.api.event.custom.PacketSendEvent(sender, packet, packetName));

        Atlas.getInstance().getEventManager().callEvent(event);
    }

    public void onPacketInAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1).replace("PacketPlayInUseItem", "PacketPlayInBlockPlace")
                .replace(Packet.Client.LEGACY_LOOK, Packet.Client.LOOK)
                .replace(Packet.Client.LEGACY_POSITION, Packet.Client.POSITION)
                .replace(Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.POSITION_LOOK);

        PacketReceiveEvent event = new PacketReceiveEvent(sender, packet, packetName);

        //EventManager.callEvent(new cc.funkemunky.api.event.custom.PacketReceiveEvent(sender, packet, packetName));

        Atlas.getInstance().getEventManager().callEvent(event);
    }

    public void injectPlayer(Player player) {
        // cc.funkemunky.api.tinyprotocol.api.impl.v1_7_R4.PlayerConnection
        Object entityPlayer = ReflectionsUtil.getEntityPlayer(player);
        val playerConnectionField = Reflection.getField(ReflectionsUtil.EntityPlayer, ReflectionsUtil.playerConnection, 0);
        val playerConnection = playerConnectionField.get(entityPlayer);
        val networkManager = Reflection.getField(ReflectionsUtil.playerConnection, ReflectionsUtil.networkManager, 0).get(playerConnection);

        try {
            playerConnectionField.set(entityPlayer, customConnection.getConstructor(ReflectionsUtil.minecraftServer, ReflectionsUtil.networkManager, ReflectionsUtil.EntityPlayer).newInstance(ReflectionsUtil.getMinecraftServer(), networkManager, entityPlayer));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static Object getPlayerConnection(Player player) {
        Object entityPlayer = ReflectionsUtil.getEntityPlayer(player);
        val playerConnectionField = Reflection.getField(ReflectionsUtil.EntityPlayer, ReflectionsUtil.playerConnection, 0);
        return playerConnectionField.get(entityPlayer);
    }
}

