package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@Deprecated
public class WrappedOutOpenWindow extends NMSObject {

    private static WrappedClass packet = Reflections.getNMSClass(Server.OPEN_WINDOW);

    public WrappedOutOpenWindow(Object object, Player player) {
        super(object, player);
    }

    private static WrappedField idField = fetchField(packet, int.class, 0);
    private static WrappedField nameField;
    private static WrappedField chatCompField;
    private static WrappedField inventorySize;

    private int id;
    private String name; //Not a thing in 1.14 and above.
    private int size;

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(idField);

        if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_13_2)) {
            name = fetch(nameField);
        }
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_19)) {
            size = getId(fetch(inventorySize));
        } else  size = fetch(inventorySize);
    }

    @Override
    public void updateObject() {

    }

    private static WrappedClass iReg;
    private static WrappedField rkeyContainers;
    private static Object resourceKeyContainers;
    private static WrappedMethod getId;

    private static int getId(Object container) {
        return getId.invoke(resourceKeyContainers, container);
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            chatCompField = fetchField(packet,
                    MinecraftReflection.iChatBaseComponent.getParent(), 0);
        }
        if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_13_2)) {
            nameField = fetchField(packet, String.class, 0);
        }
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_19)) {
            iReg = Reflections.getNMSClass("IRegistry");
            rkeyContainers = iReg.getFieldByName("ah");
            resourceKeyContainers = rkeyContainers.get(null);
            getId = iReg.getMethod("a", Object.class);
            inventorySize = fetchField(packet, Reflections.getClass("net.minecraft.world.inventory.Containers").getParent(), 0);
        } else {
            inventorySize = fetchField(packet, int.class, 1);
        }
    }
}
