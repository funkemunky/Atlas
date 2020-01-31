package cc.funkemunky.api.utils;

import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class Materials {
    private static final int[] MATERIAL_FLAGS = new int[2276];

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
        WrappedClass materialClass = new WrappedClass(Material.class);

        Material[] array = materialClass
                .getFields(field -> field.getType().equals(Material.class) && field.isAnnotationPresent(Deprecated.class))
                .stream().map(field -> (Material)field.get(null)
                ).toArray(Material[]::new);
        System.out.println("size: " + array.length);
        for (int i = 0; i < array.length; i++) {
            Material material = array[i];

            if (material.isSolid()) {
                MATERIAL_FLAGS[material.getId()] |= SOLID;
            }

            if (material.name().endsWith("_STAIRS")) {
                MATERIAL_FLAGS[material.getId()] |= STAIRS;
            }

            if (material.name().contains("SLAB") || material.name().contains("STEP")) {
                MATERIAL_FLAGS[material.getId()] |= SLABS;
            }
        }

        // fix some types where isSolid() returns the wrong value
        MATERIAL_FLAGS[63] = 0; //SIGN_POST
        MATERIAL_FLAGS[68] = 0; //WALL_SIGN
        MATERIAL_FLAGS[147] = 0; //IRON_PLATE
        MATERIAL_FLAGS[148] = 0; //GOLD_PLATE
        MATERIAL_FLAGS[72] = 0; //WOOD_PLATE
        MATERIAL_FLAGS[70] = 0; //STONE_PLATE
        MATERIAL_FLAGS[165] = SOLID;
        MATERIAL_FLAGS[93] = SOLID; //DIODE_BLOCK_OFF
        MATERIAL_FLAGS[94] = SOLID; //DIODE_BLOCK_On
        MATERIAL_FLAGS[171] = SOLID; //CARPET
        MATERIAL_FLAGS[78] = SOLID; //SNOW
        MATERIAL_FLAGS[145] = SOLID; //ANVIL
        MATERIAL_FLAGS[111] = SOLID; //WATER_LILY
        MATERIAL_FLAGS[144] = SOLID; //SKULL

        // liquids
        MATERIAL_FLAGS[8] |= LIQUID | WATER; //WATER
        MATERIAL_FLAGS[9] |= LIQUID | WATER; //STATIONARY_WATER
        MATERIAL_FLAGS[10] |= LIQUID | LAVA; //LAVA
        MATERIAL_FLAGS[11] |= LIQUID | LAVA; //STATIONARY_LAVA

        // ladders
        MATERIAL_FLAGS[65] |= LADDER | SOLID; //LADDER
        MATERIAL_FLAGS[106] |= LADDER | SOLID; //VINE

        // walls
        MATERIAL_FLAGS[85] |= WALL; //FENCE
        MATERIAL_FLAGS[107] |= WALL; //FENCE_GATE
        MATERIAL_FLAGS[139] |= WALL; //COBBLE_WALL
        MATERIAL_FLAGS[113] |= WALL; //NETHER_FENCE

        // slabs
        MATERIAL_FLAGS[26] |= SLABS; //BED_BLOCK

        // ice
        MATERIAL_FLAGS[79] |= ICE; //ICE
        MATERIAL_FLAGS[174] |= ICE; //PACKED_ICE

        for (Material mat : array) {
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
