package cc.funkemunky.api.utils.messages.modifiers;

import cc.funkemunky.api.reflection.CraftReflection;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.Tuple;
import cc.funkemunky.api.utils.messages.Modifier;
import cc.funkemunky.api.utils.messages.ModifierType;
import cc.funkemunky.carbon.utils.security.GeneralUtils;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class HoverItemModifier extends Modifier {

    public HoverItemModifier(ItemStack stack) {
        super("show_item", MiscUtils.getResult(() -> {
            try {
                return GeneralUtils.bytesToString(cc.funkemunky.carbon.utils.MiscUtils.getBytesOfObject(stack));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }));
    }

    @Override
    public ModifierType getEvent() {
        return ModifierType.HOVER_ITEM;
    }

    @Override
    public Tuple<String, String> getFormatter() {
        String key = "show_item";
        try {
            ItemStack stack = (ItemStack) cc.funkemunky.carbon.utils.MiscUtils
                    .objectFromBytes(GeneralUtils.bytesFromString(value));

            Object nms = CraftReflection.getVanillaItemStack(stack);
            String value = nms.getClass().getMethod("getTag", new Class[0]).toString();

            return new Tuple<>(key, value);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
