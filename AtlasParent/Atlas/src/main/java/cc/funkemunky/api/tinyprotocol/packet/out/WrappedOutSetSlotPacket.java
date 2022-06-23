package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@Deprecated
public class WrappedOutSetSlotPacket extends NMSObject {

    private static WrappedClass classSetSlot = Reflections.getNMSClass(Packet.Server.SET_SLOT);
    private static WrappedConstructor emptyConstructor = classSetSlot.getConstructor();
    private static WrappedField fieldWindow = fetchField(classSetSlot, int.class, 0),
            fieldSlot = fetchField(classSetSlot, int.class, 1),
            fieldItem = fetchField(classSetSlot, MinecraftReflection.itemStack.getParent(), 0);

    private int windowId, slot;
    private ItemStack item;

    public WrappedOutSetSlotPacket(int windowId, int slot, ItemStack item) {
        super((Object)emptyConstructor.newInstance());
        this.windowId = windowId;
        this.slot = slot;
        this.item = item;
        updateObject();
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        super.process(player, version);

        windowId = fetch(fieldWindow);
        slot = fetch(fieldSlot);
        item = MinecraftReflection.toBukkitItemStack(fetch(fieldItem));
    }

    @Override
    public void updateObject() {
        set(fieldWindow, windowId);
        set(fieldSlot, slot);
        set(fieldItem, CraftReflection.getVanillaItemStack(item));
    }
}
