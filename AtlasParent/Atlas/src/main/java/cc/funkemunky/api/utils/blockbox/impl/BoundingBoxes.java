package cc.funkemunky.api.utils.blockbox.impl;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public class BoundingBoxes {
    private Map<Material, BlockBox> blockBoxes = new HashMap<>();

    public void addBox() {
        List<String> strings = new ArrayList<>();


        blockBoxes.put(Material.AIR, new RegularBox(Material.AIR, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.STONE, new RegularBox(Material.STONE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.GRASS, new RegularBox(Material.GRASS, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.DIRT, new RegularBox(Material.DIRT, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.COBBLESTONE, new RegularBox(Material.COBBLESTONE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.WOOD, new RegularBox(Material.WOOD, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.SAPLING, new RegularBox(Material.SAPLING, new BoundingBox(.1f, 0f, .1f, .9f, .8f, .9f)));
        blockBoxes.put(Material.BEDROCK, new RegularBox(Material.BEDROCK, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.WATER, new RegularBox(Material.WATER, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.STATIONARY_WATER, new RegularBox(Material.STATIONARY_WATER, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.LAVA, new RegularBox(Material.LAVA, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.STATIONARY_LAVA, new RegularBox(Material.STATIONARY_LAVA, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.SAND, new RegularBox(Material.SAND, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.GRAVEL, new RegularBox(Material.GRAVEL, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.GOLD_ORE, new RegularBox(Material.GOLD_ORE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.IRON_ORE, new RegularBox(Material.IRON_ORE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.COAL_ORE, new RegularBox(Material.COAL_ORE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.LOG, new RegularBox(Material.LOG, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.LEAVES, new RegularBox(Material.LEAVES, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.SPONGE, new RegularBox(Material.SPONGE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.GLASS, new RegularBox(Material.GLASS, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.LAPIS_ORE, new RegularBox(Material.LAPIS_ORE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.LAPIS_BLOCK, new RegularBox(Material.LAPIS_BLOCK, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.DISPENSER, new RegularBox(Material.DISPENSER, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.SANDSTONE, new RegularBox(Material.SANDSTONE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.NOTE_BLOCK, new RegularBox(Material.NOTE_BLOCK, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.BED_BLOCK, new RegularBox(Material.BED_BLOCK, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.POWERED_RAIL, new RegularBox(Material.POWERED_RAIL, new BoundingBox(0f, 0f, 0f, 1f, .125f, 1f)));
        blockBoxes.put(Material.DETECTOR_RAIL, new RegularBox(Material.DETECTOR_RAIL, new BoundingBox(0f, 0f, 0f, 1f, .125f, 1f)));
        blockBoxes.put(Material.PISTON_STICKY_BASE, new PistonBox(Material.PISTON_STICKY_BASE));
        blockBoxes.put(Material.WEB, new RegularBox(Material.WEB, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.PISTON_BASE, new PistonBox(Material.PISTON_BASE));
        blockBoxes.put(Material.PISTON_EXTENSION, new PistonBox(Material.PISTON_EXTENSION));
        blockBoxes.put(Material.WOOL, new RegularBox(Material.WOOL, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.PISTON_MOVING_PIECE, new PistonBox(Material.PISTON_MOVING_PIECE));
        blockBoxes.put(Material.YELLOW_FLOWER, new RegularBox(Material.YELLOW_FLOWER, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.RED_ROSE, new RegularBox(Material.RED_ROSE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.BROWN_MUSHROOM, new RegularBox(Material.BROWN_MUSHROOM, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.RED_MUSHROOM, new RegularBox(Material.RED_MUSHROOM, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.GOLD_BLOCK, new RegularBox(Material.GOLD_BLOCK, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.IRON_BLOCK, new RegularBox(Material.IRON_BLOCK, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.BRICK, new RegularBox(Material.BRICK, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.TNT, new RegularBox(Material.TNT, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.BOOKSHELF, new RegularBox(Material.BOOKSHELF, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.MOSSY_COBBLESTONE, new RegularBox(Material.MOSSY_COBBLESTONE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.OBSIDIAN, new RegularBox(Material.OBSIDIAN, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.FIRE, new RegularBox(Material.FIRE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.MOB_SPAWNER, new RegularBox(Material.MOB_SPAWNER, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.DIAMOND_ORE, new RegularBox(Material.DIAMOND_ORE, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.DIAMOND_BLOCK, new RegularBox(Material.DIAMOND_BLOCK, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        blockBoxes.put(Material.WORKBENCH, new RegularBox(Material.WORKBENCH, new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));
        Arrays.stream(Material.values()).filter(Material::isBlock).forEach(material -> {
            strings.add("blockBoxes.put(Material." + material.toString() + ", new RegularBox(Material." + material.toString() + ", new BoundingBox(0f, 0f, 0f, 1f, 1f, 1f)));");
        });

        StringBuilder bodyBuilder = new StringBuilder();

        strings.forEach(line -> bodyBuilder.append(line).append("\n"));
    }

    public List<BoundingBox> getBoundingBox(Block block) {
        if(blockBoxes.containsKey(block.getType())) {
            return blockBoxes.get(block.getType()).getBox(block);
        } else if(BlockUtils.isStair(block)) {
            return new StairBox(block.getType()).getBox(block);
        } else if(BlockUtils.isPiston(block)) {
            return new PistonBox(block.getType()).getBox(block);
        } else {
            return Atlas.getInstance().getBlockBoxManager().getBlockBox().getSpecificBox(block.getLocation());
        }
    }
}
