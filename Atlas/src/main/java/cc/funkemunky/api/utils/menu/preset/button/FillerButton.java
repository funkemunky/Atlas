package cc.funkemunky.api.utils.menu.preset.button;

import cc.funkemunky.api.utils.ItemBuilder;
import cc.funkemunky.api.utils.XMaterial;
import cc.funkemunky.api.utils.menu.button.Button;

public class FillerButton extends Button {

    public FillerButton() {
        super(false, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).name(" ")
                .durability(15).build());
    }
}
