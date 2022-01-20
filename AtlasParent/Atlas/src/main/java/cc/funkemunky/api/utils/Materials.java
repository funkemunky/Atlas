package cc.funkemunky.api.utils;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
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
    public static final int LIQUID = 0b00000000000000000000010000000;
    public static final int ICE    = 0b00000000000000000000100000000;
    public static final int FENCE  = 0b00000000000000000001000000000;

    static {
        for (int i = 0; i < MATERIAL_FLAGS.length; i++) {
            Material material = Material.values()[i];

            //We use the one in BlockUtils also since we can't trust Material to include everything.
            if (material.isSolid() || material.name().contains("COMPARATOR") || material.name().contains("DIODE")) {
                MATERIAL_FLAGS[i] |= SOLID;
            }
            if (material.name().endsWith("_STAIRS")) {
                MATERIAL_FLAGS[i] |= STAIRS;
            }

            if (material.name().contains("SLAB") || material.name().contains("STEP")) {
                MATERIAL_FLAGS[i] |= SLABS;
            }

            if(material.name().contains("SKULL"))
                MATERIAL_FLAGS[i] = SOLID;

            if(material.name().contains("STATIONARY") || material.name().contains("LAVA") || material.name().contains("WATER")) {
                if(material.name().contains("LAVA")) {
                    MATERIAL_FLAGS[i] |= LIQUID | LAVA;
                } else MATERIAL_FLAGS[i] |= LIQUID | WATER;
            }
        }

        // fix some types where isSolid() returns the wrong value
        MATERIAL_FLAGS[XMaterial.REPEATER.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.SNOW.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.ANVIL.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.LILY_PAD.parseMaterial().ordinal()] = SOLID;

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            MATERIAL_FLAGS[XMaterial.SLIME_BLOCK.parseMaterial().ordinal()] = SOLID;

            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_14)) {
                MATERIAL_FLAGS[XMaterial.SCAFFOLDING.parseMaterial().ordinal()] = SOLID;
            }
        }

        // ladders
        MATERIAL_FLAGS[XMaterial.LADDER.parseMaterial().ordinal()] |= LADDER | SOLID;
        MATERIAL_FLAGS[XMaterial.VINE.parseMaterial().ordinal()] |= LADDER | SOLID;
        for (Material mat : Material.values()) {
            if(!mat.isBlock()) continue;
            if (mat.name().contains("FENCE")) {
                if(!mat.name().contains("GATE")) MATERIAL_FLAGS[mat.ordinal()] |= FENCE;
            }
            if(mat.name().contains("WALL")) MATERIAL_FLAGS[mat.ordinal()] |= WALL;
            if(mat.name().contains("PLATE")) MATERIAL_FLAGS[mat.ordinal()] = 0;
            if(mat.name().contains("BED") && !mat.name().contains("ROCK")) MATERIAL_FLAGS[mat.ordinal()]  |= SLABS;
            if(mat.name().contains("ICE")) MATERIAL_FLAGS[mat.ordinal()] |= ICE;
            if(mat.name().contains("CARPET")) MATERIAL_FLAGS[mat.ordinal()] = SOLID;
            if(mat.name().contains("SIGN")) MATERIAL_FLAGS[mat.ordinal()] = 0;
        }
    }

    public static int getBitmask(Material material) {
        return MATERIAL_FLAGS[material.ordinal()];
    }

    private Materials() {

    }

    public static boolean checkFlag(Material material, int flag) {
        return (MATERIAL_FLAGS[material.ordinal()] & flag) == flag;
    }

    public static boolean isUsable(Material material) {
        String nameLower = material.name().toLowerCase();
        return material.isEdible()
                || nameLower.contains("bow")
                || nameLower.contains("sword")
                || nameLower.contains("trident");
    }

}