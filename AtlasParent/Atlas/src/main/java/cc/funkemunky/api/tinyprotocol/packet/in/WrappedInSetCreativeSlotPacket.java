package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.BukkitReflection;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//TODO Test
public class WrappedInSetCreativeSlotPacket extends NMSObject {

    private static WrappedClass setCreativeClass = Reflections.getNMSClass(Client.CREATIVE_SLOT);
    private static WrappedField slotField = setCreativeClass.getFieldByType(int.class, 0),
            itemStackField = setCreativeClass.getFieldByType(MinecraftReflection.itemStack.getParent(), 0);

    public WrappedInSetCreativeSlotPacket(Object object, Player player) {
        super(object, player);
    }

    public int slot;
    public ItemStack itemStack;

    @Override
    public void updateObject() {
        setPacket(Client.CREATIVE_SLOT, slot, CraftReflection.getVanillaItemStack(itemStack));
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        slot = fetch(slotField);
        itemStack = BukkitReflection.getBukkitStackFromVanilla(fetch(itemStackField));
    }
}
