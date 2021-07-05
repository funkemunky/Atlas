package cc.funkemunky.api.utils.menu.preset;

import cc.funkemunky.api.utils.MathHelper;
import cc.funkemunky.api.utils.menu.preset.button.SettingButton;
import cc.funkemunky.api.utils.menu.type.impl.ChestMenu;

public class SettingsMenu extends ChestMenu {
    public SettingsMenu(String title, SettingButton... buttons) {
        super(title, MathHelper.ceiling_float_int(buttons.length / 9f));

        for (int i = 0; i < buttons.length; i++) {
            setItem(i, buttons[i]);
        }
    }
}
