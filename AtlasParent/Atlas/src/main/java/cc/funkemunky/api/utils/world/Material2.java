package cc.funkemunky.api.utils.world;

import org.bukkit.Material;

@SuppressWarnings("unused")
public final class Material2 {

    /*
     * Any Material past <minSupportVersion> should be put here, that way if it doesn't exist its null rather than classdefnotfound
     *
     */

    // 1.8+
    public static final Material DAYLIGHT_DETECTOR_INVERTED = m(178);
    public static final Material SLIME_BLOCK = m(165);

    // 1.9+
    public static final Material END_ROD = m(198);
    public static final Material CHORUS_PLANT = m(199);
    public static final Material CHORUS_FLOWER = m(200);
    public static final Material PURPUR_BLOCK = m(201);
    public static final Material PURPUR_PILLAR = m(202);
    public static final Material PURPUR_STAIRS = m(203);
    public static final Material PURPUR_DOUBLE_SLAB = m(204);
    public static final Material PURPUR_SLAB = m(205);
    public static final Material END_BRICKS = m(206);
    public static final Material BEETROOT_BLOCK = m(207);
    public static final Material GRASS_PATH = m(208);
    public static final Material END_GATEWAY = m(209);
    public static final Material COMMAND_REPEATING = m(210);
    public static final Material COMMAND_CHAIN = m(211);
    public static final Material FROSTED_ICE = m(212);
    public static final Material MAGMA = m(213);
    public static final Material NETHER_WART_BLOCK = m(214);
    public static final Material RED_NETHER_BRICK = m(215);
    public static final Material BONE_BLOCK = m(216);
    public static final Material STRUCTURE_VOID = m(217);
    public static final Material OBSERVER = m(218);
    public static final Material WHITE_SHULKER_BOX = m(219);
    public static final Material ORANGE_SHULKER_BOX = m(220);
    public static final Material MAGENTA_SHULKER_BOX = m(221);
    public static final Material LIGHT_BLUE_SHULKER_BOX = m(222);
    public static final Material YELLOW_SHULKER_BOX = m(223);
    public static final Material LIME_SHULKER_BOX = m(224);
    public static final Material PINK_SHULKER_BOX = m(225);
    public static final Material GRAY_SHULKER_BOX = m(226);
    public static final Material SILVER_SHULKER_BOX = m(227);
    public static final Material CYAN_SHULKER_BOX = m(228);
    public static final Material PURPLE_SHULKER_BOX = m(229);
    public static final Material BLUE_SHULKER_BOX = m(230);
    public static final Material BROWN_SHULKER_BOX = m(231);
    public static final Material GREEN_SHULKER_BOX = m(232);
    public static final Material RED_SHULKER_BOX = m(233);
    public static final Material BLACK_SHULKER_BOX = m(234);
    public static final Material WHITE_GLAZED_TERRACOTTA = m(235);
    public static final Material ORANGE_GLAZED_TERRACOTTA = m(236);
    public static final Material MAGENTA_GLAZED_TERRACOTTA = m(237);
    public static final Material LIGHT_BLUE_GLAZED_TERRACOTTA = m(238);
    public static final Material YELLOW_GLAZED_TERRACOTTA = m(239);
    public static final Material LIME_GLAZED_TERRACOTTA = m(240);
    public static final Material PINK_GLAZED_TERRACOTTA = m(241);
    public static final Material GRAY_GLAZED_TERRACOTTA = m(242);
    public static final Material SILVER_GLAZED_TERRACOTTA = m(243);
    public static final Material CYAN_GLAZED_TERRACOTTA = m(244);
    public static final Material PURPLE_GLAZED_TERRACOTTA = m(245);
    public static final Material BLUE_GLAZED_TERRACOTTA = m(246);
    public static final Material BROWN_GLAZED_TERRACOTTA = m(247);
    public static final Material GREEN_GLAZED_TERRACOTTA = m(248);
    public static final Material RED_GLAZED_TERRACOTTA = m(249);
    public static final Material BLACK_GLAZED_TERRACOTTA = m(250);
    public static final Material CONCRETE = m(251);
    public static final Material CONCRETE_POWDER = m(252);
    public static final Material STRUCTURE_BLOCK = m(255);
    public static final Material END_CRYSTAL = m(426);
    public static final Material CHORUS_FRUIT = m(432);
    public static final Material CHORUS_FRUIT_POPPED = m(433);
    public static final Material BEETROOT = m(434);
    public static final Material BEETROOT_SEEDS = m(435);
    public static final Material BEETROOT_SOUP = m(436);
    public static final Material DRAGONS_BREATH = m(437);
    public static final Material SPLASH_POTION = m(438);
    public static final Material SPECTRAL_ARROW = m(439);
    public static final Material TIPPED_ARROW = m(440);
    public static final Material LINGERING_POTION = m(441);
    public static final Material SHIELD = m(442);
    public static final Material ELYTRA = m(443);
    public static final Material BOAT_SPRUCE = m(444);
    public static final Material BOAT_BIRCH = m(445);
    public static final Material BOAT_JUNGLE = m(446);
    public static final Material BOAT_ACACIA = m(447);
    public static final Material BOAT_DARK_OAK = m(448);
    public static final Material TOTEM = m(449);
    public static final Material SHULKER_SHELL = m(450);
    public static final Material IRON_NUGGET = m(452);
    public static final Material KNOWLEDGE_BOOK = m(453);

    private static Material m(String name) {
        return Material.getMaterial(name);
    }

    private static Material m(int id) {
        return Material.getMaterial(id);
    }

}
