package cc.funkemunky.api.utils.menu.preset.button;

import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.ItemBuilder;
import cc.funkemunky.api.utils.Setting;
import cc.funkemunky.api.utils.menu.button.Button;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class SettingButton extends Button {

    private Setting<?> setting;
    private AtomicInteger current;

    public SettingButton(Player player, String name, String description, Setting<?> setting) {
        super(false, new ItemBuilder(Material.BOOK)
                .amount(1).name(Color.Gold + name)
                .lore(description, "&7Current State: &6" +
                        (setting.getValue(player) instanceof Enum
                                ? ((Enum)setting.getValue(player)).name() : setting.getValue(player)))
                .build());

        this.setting = setting;

        Object currentVal = setting.getValue(player);

        current = new AtomicInteger(0);
        for (int i = 0; i < setting.options.length; i++) {
            if(setting.options[i].equals(currentVal)) {
                current = new AtomicInteger(i);
                break;
            }
        }

        setConsumer((pl, info) -> {
            if(info.getClickType().isLeftClick()) {
                if(current.incrementAndGet() > setting.options.length - 1) {
                    current.set(0);
                }

                int i = current.get();

                String stringVal = setting.options[i] instanceof Enum
                        ? ((Enum)setting.options[i]).name() : String.valueOf(setting.options[i]);
                val stack = new ItemBuilder(Material.BOOK)
                        .amount(1).name(Color.Gold + name)
                        .lore(description, "&7Current State: &6" + stringVal)
                        .build();

                pl.sendMessage(Color.Green + "Option set to " + stringVal);

                info.getButton().getStack().setItemMeta(stack.getItemMeta());

                info.getMenu().buildInventory(false);

                SettingButton.this.setting.setValue(pl, setting.options[i]);
            }
        });
    }
}