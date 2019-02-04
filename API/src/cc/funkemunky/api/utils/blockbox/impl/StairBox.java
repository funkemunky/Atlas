package cc.funkemunky.api.utils.blockbox.impl;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;

public class StairBox extends BlockBox {
    StairBox(Material material, BoundingBox original) {
        super(material, original);
    }

    @Override
    BoundingBox getBox(Block block) {
        Stairs stairs = (Stairs) block.getState();

        StringBuilder boxes = new StringBuilder();

        return getOriginal();
    }
}
