package cc.funkemunky.api.utils;

import org.bukkit.Material;

public class Materials {
    private static final int[] MATERIAL_FLAGS = new int[Material.values().length];

    public static final int SOLID  = 0b00000000000000000000000000001;
    public static final int LADDER = 0b00000000000000000000000000010;
    public static final int WALL   = 0b00000000000000000000000000100;
    public static final int STAIRS = 0b00000000000000000000000001000;
    public static final int SLABS  = 0b00000000000000000000000010000;
    public static final int WATER  = 0b00000000000000000000000100000;
    public static final int LAVA   = 0b00000000000000000000001000000;
    public static final int LIQUID = 0b00000000000000000000001100000;
    public static final int ICE    = 0b00000000000000000000010000000;
    public static final int FENCE  = 0b00000000000000000000100000000;

    static {
        for (int i = 0; i < MATERIAL_FLAGS.length; i++) {
            Material material = Material.values()[i];

            if (material.isSolid()) {
                MATERIAL_FLAGS[i] |= SOLID;
            }

            if (material.name().endsWith("_STAIRS")) {
                MATERIAL_FLAGS[i] |= STAIRS;
            }

            if (material.name().contains("SLAB") || material.name().contains("STEP")) {
                MATERIAL_FLAGS[i] |= SLABS;
            }
        }

        // fix some types where isSolid() returns the wrong value
        MATERIAL_FLAGS[MiscUtils.match("SIGN_POST").getId()] = 0;
        MATERIAL_FLAGS[Material.WALL_SIGN.getId()] = 0;
        MATERIAL_FLAGS[MiscUtils.match("GOLD_PLATE").getId()] = 0;
        MATERIAL_FLAGS[MiscUtils.match("IRON_PLATE").getId()] = 0;
        MATERIAL_FLAGS[MiscUtils.match("WOOD_PLATE").getId()] = 0;
        MATERIAL_FLAGS[MiscUtils.match("STONE_PLATE").getId()] = 0;
        MATERIAL_FLAGS[165] = SOLID;
        MATERIAL_FLAGS[MiscUtils.match("DIODE_BLOCK_OFF").getId()] = SOLID;
        MATERIAL_FLAGS[MiscUtils.match("DIODE_BLOCK_ON").getId()] = SOLID;
        MATERIAL_FLAGS[MiscUtils.match("CARPET").getId()] = SOLID;
        MATERIAL_FLAGS[Material.SNOW.getId()] = SOLID;
        MATERIAL_FLAGS[Material.ANVIL.getId()] = SOLID;
        MATERIAL_FLAGS[MiscUtils.match("WATER_LILY").getId()] = SOLID;
        MATERIAL_FLAGS[MiscUtils.match("SKULL").getId()] = SOLID;

        // liquids
        MATERIAL_FLAGS[Material.WATER.getId()] |= LIQUID | WATER;
        MATERIAL_FLAGS[MiscUtils.match("STATIONARY_WATER").getId()] |= LIQUID | WATER;
        MATERIAL_FLAGS[Material.LAVA.getId()] |= LIQUID | LAVA;
        MATERIAL_FLAGS[MiscUtils.match("STATIONARY_LAVA").getId()] |= LIQUID | LAVA;

        // ladders
        MATERIAL_FLAGS[Material.LADDER.getId()] |= LADDER | SOLID;
        MATERIAL_FLAGS[Material.VINE.getId()] |= LADDER | SOLID;

        // walls
        MATERIAL_FLAGS[MiscUtils.match("FENCE").getId()] |= WALL;
        MATERIAL_FLAGS[MiscUtils.match("FENCE_GATE").getId()] |= WALL;
        MATERIAL_FLAGS[MiscUtils.match("COBBLE_WALL").getId()] |= WALL;
        MATERIAL_FLAGS[MiscUtils.match("NETHER_FENCE").getId()] |= WALL;

        // slabs
        MATERIAL_FLAGS[MiscUtils.match("BED_BLOCK").getId()] |= SLABS;

        // ice
        MATERIAL_FLAGS[Material.ICE.getId()] |= ICE;
        MATERIAL_FLAGS[Material.PACKED_ICE.getId()] |= ICE;

        for (Material mat : Material.values()) {
            if (mat.name().contains("FENCE")) MATERIAL_FLAGS[mat.getId()] |= FENCE;
        }
    }

    public static int getBitmask(Material material) {
        return MATERIAL_FLAGS[material.getId()];
    }

    private Materials() {
    }

    public static boolean checkFlag(Material material, int flag) {
        return (MATERIAL_FLAGS[material.getId()] & flag) == flag;
    }

    public static boolean isUsable(Material material) {
        String nameLower = material.name().toLowerCase();
        return material.isEdible()
                || nameLower.contains("bow")
                || nameLower.contains("sword")
                || nameLower.contains("trident");
    }

}