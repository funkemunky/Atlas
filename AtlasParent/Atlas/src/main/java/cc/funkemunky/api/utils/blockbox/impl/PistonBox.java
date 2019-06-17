package cc.funkemunky.api.utils.blockbox.impl;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.ReflectionsUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PistonBox extends BlockBox {
    public PistonBox(Material material) {
        super(material, new BoundingBox(0,0,0,1,1,1));
    }

    @Override
    List<BoundingBox> getBox(Block block) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            switch(getMaterial()) {
                case PISTON_BASE:
                case PISTON_STICKY_BASE: {
                    org.bukkit.material.PistonBaseMaterial piston = (org.bukkit.material.PistonBaseMaterial) block.getType().getNewData(block.getData());

                    if (!piston.isPowered()) {
                        return Collections.singletonList(getOriginal().add(block.getLocation().toVector()));
                    } else {
                        switch (piston.getFacing()) {
                            case DOWN:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                            case UP:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F).add(block.getLocation().toVector()));
                            case NORTH:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                            case SOUTH:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F).add(block.getLocation().toVector()));
                            case WEST:
                                return Collections.singletonList(new BoundingBox(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                            case EAST:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                        }
                    }
                }
                case PISTON_EXTENSION:
                case PISTON_MOVING_PIECE: {
                    org.bukkit.material.PistonExtensionMaterial piston = (org.bukkit.material.PistonExtensionMaterial) block.getType().getNewData(block.getData());

                    switch(piston.getFacing()) {
                        case DOWN:
                            return Arrays.asList(new BoundingBox(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F).add(block.getLocation().toVector()), new BoundingBox(0,0,0,1,.25f,1).add(block.getLocation().toVector()));
                        case UP:
                            return Arrays.asList(new BoundingBox(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F).add(block.getLocation().toVector()), new BoundingBox(0,.75f,0,1,1,1).add(block.getLocation().toVector()));
                        case NORTH:
                            return Arrays.asList(new BoundingBox(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F).add(block.getLocation().toVector()), new BoundingBox(0,0,0,1,1,.25f).add(block.getLocation().toVector()));
                        case SOUTH:
                            return Arrays.asList(new BoundingBox(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F).add(block.getLocation().toVector()), new BoundingBox(0,0,.75f,1,1,1).add(block.getLocation().toVector()));
                        case WEST:
                            return Arrays.asList(new BoundingBox(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F).add(block.getLocation().toVector()), new BoundingBox(0,0,0,.25f,1,1).add(block.getLocation().toVector()));
                        case EAST:
                            return Arrays.asList(new BoundingBox(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F).add(block.getLocation().toVector()), new BoundingBox(.75f, 0, 0, 1,1,1).add(block.getLocation().toVector()));
                    }
                }
            }
        } else {
            BlockData data = (BlockData) ReflectionsUtil.getMethodValue(ReflectionsUtil.getMethod(ReflectionsUtil.getClass("org.bukkit.block.Block"), "getBlockData"), block);
            org.bukkit.block.data.type.Piston piston = (org.bukkit.block.data.type.Piston) data;

            switch(getMaterial()) {
                case PISTON_BASE:
                case PISTON_STICKY_BASE: {
                    if (!piston.isExtended()) {
                        return Collections.singletonList(getOriginal());
                    } else {
                        switch (piston.getFacing()) {
                            case DOWN:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                            case UP:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F).add(block.getLocation().toVector()));
                            case NORTH:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                            case SOUTH:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F).add(block.getLocation().toVector()));
                            case WEST:
                                return Collections.singletonList(new BoundingBox(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                            case EAST:
                                return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                        }
                    }
                }
                case PISTON_EXTENSION:
                case PISTON_MOVING_PIECE: {
                    switch(piston.getFacing()) {
                        case DOWN:
                            return Arrays.asList(new BoundingBox(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F).add(block.getLocation().toVector()), new BoundingBox(0,0,0,1,.25f,1).add(block.getLocation().toVector()));
                        case UP:
                            return Arrays.asList(new BoundingBox(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F).add(block.getLocation().toVector()), new BoundingBox(0,.75f,0,1,1,1).add(block.getLocation().toVector()));
                        case NORTH:
                            return Arrays.asList(new BoundingBox(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F).add(block.getLocation().toVector()), new BoundingBox(0,0,0,1,1,.25f).add(block.getLocation().toVector()));
                        case SOUTH:
                            return Arrays.asList(new BoundingBox(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F).add(block.getLocation().toVector()), new BoundingBox(0,0,.75f,1,1,1).add(block.getLocation().toVector()));
                        case WEST:
                            return Arrays.asList(new BoundingBox(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F).add(block.getLocation().toVector()), new BoundingBox(0,0,0,.25f,1,1).add(block.getLocation().toVector()));
                        case EAST:
                            return Arrays.asList(new BoundingBox(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F).add(block.getLocation().toVector()), new BoundingBox(.75f, 0, 0, 1,1,1).add(block.getLocation().toVector()));
                    }
                }
            }
        }
        return Collections.singletonList(getOriginal());
    }
}
