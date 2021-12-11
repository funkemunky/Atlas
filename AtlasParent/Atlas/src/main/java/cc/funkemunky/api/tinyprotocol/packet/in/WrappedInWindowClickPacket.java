package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class WrappedInWindowClickPacket extends NMSObject {
    private static final String packet = Client.WINDOW_CLICK;

    // Fields
    private static FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static FieldAccessor<Integer> fieldSlot = fetchField(packet, int.class, 1);
    private static FieldAccessor<Integer> fieldButton = fetchField(packet, int.class, 2);
    private static WrappedField fieldAction = fetchField(Reflections.getNMSClass(packet), (ProtocolVersion.getGameVersion()
            .isOrAbove(ProtocolVersion.v1_17) ? int.class : short.class), ProtocolVersion.getGameVersion()
            .isOrAbove(ProtocolVersion.v1_17) ? 3 : 0);
    private static FieldAccessor<Object> fieldItemStack = fetchField(packet,
            MinecraftReflection.itemStack.getParent(), 0);

    // Decoded data
    private int id;
    private short slot;
    private int button;
    private short counter;
    private ClickType action;
    private ItemStack item;
    private int mode;

    public WrappedInWindowClickPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(fieldId);
        slot = fetch(fieldSlot).shortValue();
        byte button = (byte)(this.button = fetch(fieldButton));
        counter = (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17) ? ((Integer)fetch(fieldAction)).shortValue() : fetch(fieldAction));
        item = toBukkitStack(fetch(fieldItemStack));

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            FieldAccessor<Integer> fieldShift = fetchField(packet, int.class, 3);
            mode = fetch(fieldShift);
        } else {
            FieldAccessor<Enum> fieldShift = fetchField(packet, Enum.class, 0);
            mode = fetch(fieldShift).ordinal();
        }

        if (slot == -1) {
            action = button == 0 ? ClickType.WINDOW_BORDER_LEFT : ClickType.WINDOW_BORDER_RIGHT;
        } else if (mode == 0) {
            if (button == 0) {
                action = ClickType.LEFT;
            } else if (button == 1) {
                action = ClickType.RIGHT;
            }
        } else if (mode == 1) {
            if (button == 0) {
                action = ClickType.SHIFT_LEFT;
            } else if (button == 1) {
                action = ClickType.SHIFT_RIGHT;
            }
        } else if (mode == 2) {
            if (button >= 0 && button < 9) {
                action = ClickType.NUMBER_KEY;
            }
        } else if (mode == 3) {
            if (button == 2) {
                action = ClickType.MIDDLE;
            } else {
                action = ClickType.UNKNOWN;
            }
        } else if (mode == 4) {
            if (slot >= 0) {
                if (button == 0) {
                    action = ClickType.DROP;
                } else if (button == 1) {
                    action = ClickType.CONTROL_DROP;
                }
            } else {
                // Sane default (because this happens when they are holding nothing. Don't ask why.)
                action = ClickType.LEFT;
                if (button == 1) {
                    action = ClickType.RIGHT;
                }
            }
        } else if (mode == 5) {
            action = ClickType.DRAG;
        } else if (mode == 6) {
            action = ClickType.DOUBLE_CLICK;
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            setPacket(packet, id, slot, button, counter, CraftReflection.getVanillaItemStack(item), mode);
        } else {
            setPacket(packet, id, slot, button, ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)
                    ? (int)counter : counter, CraftReflection.getVanillaItemStack(item));
        }
    }

    public enum ClickType {
        LEFT,
        SHIFT_LEFT,
        RIGHT,
        SHIFT_RIGHT,
        WINDOW_BORDER_LEFT,
        WINDOW_BORDER_RIGHT,
        MIDDLE,
        NUMBER_KEY,
        DOUBLE_CLICK,
        DROP,
        CONTROL_DROP,
        CREATIVE,
        DRAG,
        UNKNOWN;

        public boolean isKeyboardClick() {
            return this == NUMBER_KEY || this == DROP || this == CONTROL_DROP;
        }

        public boolean isCreativeAction() {
            return this == MIDDLE || this == CREATIVE;
        }

        public boolean isRightClick() {
            return this == RIGHT || this == SHIFT_RIGHT;
        }

        public boolean isLeftClick() {
            return this == LEFT || this == SHIFT_LEFT || this == DOUBLE_CLICK || this == CREATIVE;
        }
        public boolean isShiftClick() {
            return this == SHIFT_LEFT || this == SHIFT_RIGHT || this == CONTROL_DROP;
        }
    }
}
