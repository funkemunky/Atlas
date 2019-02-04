package cc.funkemunky.api.utils.blockbox.impl;

import cc.funkemunky.api.utils.BoundingBox;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class RegularBox extends BlockBox {
    public RegularBox(Material material, BoundingBox original) {
        super(material, original);
    }

    @Override
    BoundingBox getBox(Block block) {
        return getOriginal();
    }
}
