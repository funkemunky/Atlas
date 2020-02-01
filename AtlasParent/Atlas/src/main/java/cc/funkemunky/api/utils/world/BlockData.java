package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.XMaterial;
import cc.funkemunky.api.utils.world.blocks.*;
import cc.funkemunky.api.utils.world.types.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.material.Cake;
import org.bukkit.material.Gate;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.Material.*;

public enum BlockData {
    _VINE(NoCollisionBox.INSTANCE,
            VINE),

    _LIQUID(new SimpleCollisionBox(0, 0, 0, 1, 0.9, 1),
            WATER, LAVA, MiscUtils.match("STATIONARY_LAVA"), MiscUtils.match("STATIONARY_WATER")),

    _BREWINGSTAND(new ComplexCollisionBox(
            new SimpleCollisionBox(0, 0, 0, 1, 0.125, 1),                      //base
            new SimpleCollisionBox(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625) //top
    ), BREWING_STAND),

    _ANVIL((protocol, b) -> {
        BlockState state = b.getState();
        b.setType(Material.ANVIL, true);
        int dir = state.getData().getData() & 0b01;
        CollisionBox box;
        if (dir == 1) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        } else {
            box = new SimpleCollisionBox(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        }
        return box;
    }, ANVIL),

    _SKULL((protocol, b) -> {
        int rotation = b.getState().getData().getData() & 7;

        CollisionBox box;
        switch (rotation) {
            case 1:
            default:
                box = new SimpleCollisionBox(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;
            case 2:
                box = new SimpleCollisionBox(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                break;
            case 3:
                box = new SimpleCollisionBox(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                break;
            case 4:
                box = new SimpleCollisionBox(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;
            case 5:
                box = new SimpleCollisionBox(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }
        return box;
    }, MiscUtils.match("SKULL")),

    _DOOR(new DoorHandler(), Arrays.stream(Material.values())
            .filter(mat -> mat.name().contains("DOOR"))
            .toArray(Material[]::new)),

    _HOPPER(new HopperBounding(), HOPPER),
    _CAKE((protocol, block) -> {
        Cake cake = (Cake) block.getType().getNewData(block.getData());

        double f1 = (1 + cake.getSlicesEaten() * 2) / 16D;

        return new SimpleCollisionBox(f1, 0, 0.0625, 1 - 0.0625, 0.5, 1 - 0.0625);
    }, MiscUtils.match("CAKE"), MiscUtils.match("CAKE_BLOCK")),

    _LADDER((protocol, b) -> {
        CollisionBox box = NoCollisionBox.INSTANCE;
        float var3 = 0.125F;

        byte data = b.getState().getData().getData();
        if (data == 2) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 1.0F - var3, 1.0F, 1.0F, 1.0F);
        } else if (data == 3) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var3);
        } else if (data == 4) {
            box = new SimpleCollisionBox(1.0F - var3, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else if (data == 5) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, var3, 1.0F, 1.0F);
        }
        return box;
    }, LADDER),

    _FENCE_GATE((protocol, b) -> {
        byte var5 = b.getState().getData().getData();

        CollisionBox box = NoCollisionBox.INSTANCE;
        if (!((Gate) b.getState().getData()).isOpen()) {
            if (var5 != 2 && var5 != 0) {
                box = new SimpleCollisionBox(0.375F, 0.0F, 0.0F, 0.625F, 1.5F, 1.0F);
            } else {
                box = new SimpleCollisionBox(0.0F, 0.0F, 0.375F, 1.0F, 1.5F, 0.625F);
            }
        }
        return box;
    }, Arrays.stream(Material.values()).filter(mat -> mat.name().contains("FENCE_GATE")).toArray(Material[]::new)),

    _FENCE(new DynamicFence(), Arrays.stream(Material.values())
            .filter(mat -> mat.name().equals("FENCE") || mat.name().endsWith("FENCE")).toArray(Material[]::new)),
    _PANE(new DynamicPane(), MiscUtils.match("THIN_GLASS"), MiscUtils.match("STAINED_GLASS_PANE"),
            MiscUtils.match("IRON_FENCE")),
    _WALL(new DynamicWall(), Arrays.stream(Material.values())
            .filter(mat -> mat.name().contains("WALL"))
            .toArray(Material[]::new)),


    _SNOW((protocol, b) -> {
        MaterialData state = b.getState().getData();
        int height = (state.getData() & 0b1111);
        if (height == 0) return new SimpleCollisionBox(0, 0, 0, 1, 0, 1); // return NoCollisionBox.INSTANCE;
        return new SimpleCollisionBox(0, 0, 0, 1, height * 0.125, 1);
    }, Material.SNOW),

    _SLAB((protocol, b) -> {
        MaterialData state = b.getState().getData();
        if ((state.getData() & 8) == 0)
            return new SimpleCollisionBox(0, 0, 0, 1, .5, 1);
        else return new SimpleCollisionBox(0, .5, 0, 1, 1, 1);
    }, Arrays.stream(Material.values()).filter(mat ->
            mat.name().contains("STEP") || mat.name().contains("SLAB")).toArray(Material[]::new)),

    _STAIR((protocol, b) -> {
        MaterialData state = b.getState().getData();
        boolean inverted = (state.getData() & 4) != 0;
        int dir = (state.getData() & 0b11);
        SimpleCollisionBox top;
        SimpleCollisionBox bottom = new SimpleCollisionBox(0, 0, 0, 1, .5, 1);
        if (dir == 0) top = new SimpleCollisionBox(.5, .5, 0, 1, 1, 1);
        else if (dir == 1) top = new SimpleCollisionBox(0, .5, 0, .5, 1, 1);
        else if (dir == 2) top = new SimpleCollisionBox(0, .5, .5, 1, 1, 1);
        else top = new SimpleCollisionBox(0, .5, 0, 1, 1, .5);
        if (inverted) {
            top.offset(0, -.5, 0);
            bottom.offset(0, .5, 0);
        }
        return new ComplexCollisionBox(top, bottom);
    }, Arrays.stream(Material.values()).filter(mat -> mat.name().contains("STAIRS")).toArray(Material[]::new)),

    _CHEST(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.125, 1).expand(-0.125, 0, -0.125),
            CHEST, TRAPPED_CHEST, ENDER_CHEST),
    _ETABLE(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.25, 1),
            MiscUtils.match("ENCHANTMENT_TABLE")),
    _FRAME(new SimpleCollisionBox(0, 0, 0, 1, 1 - (0.0625 * 3), 1),
            MiscUtils.match("ENDER_PORTAL_FRAME")),

    _CARPET(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F), MiscUtils.match("CARPET")),
    _Daylight(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.375, 1.0F),
            MiscUtils.match("DAYLIGHT_DETECTOR"), MiscUtils.match("DAYLIGHT_DETECTOR_INVERTED")),
    _LILIPAD((v, b) -> {
        if (v.isBelow(ProtocolVersion.V1_9))
            return new SimpleCollisionBox(0.0f, 0.0F, 0.0f, 1.0f, 0.015625F, 1.0f);
        return new SimpleCollisionBox(0.0625, 0.0F, 0.0625, 0.9375, 0.015625F, 0.9375);
    }, MiscUtils.match("WATER_LILY")),

    _BED(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625, 1.0F),
            MiscUtils.match("BED_BLOCK")),


    _TRAPDOOR(new TrapDoorHandler(), Arrays.stream(Material.values())
            .filter(mat -> mat.name().contains("TRAP_DOOR")).toArray(Material[]::new)),

    _STUPID(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F),
            MiscUtils.match("DIODE_BLOCK_OFF"), MiscUtils.match("DIODE_BLOCK_ON"),
            MiscUtils.match("REDSTONE_COMPARATOR_ON"), MiscUtils.match("REDSTONE_COMPARATOR_OFF")),

    _STRUCTURE_VOID(new SimpleCollisionBox(0.375, 0.375, 0.375, 0.625, 0.625, 0.625), Material2.STRUCTURE_VOID), //
    _END_ROD(new DynamicRod(), Material2.END_ROD),
    _CAULDRON(new CouldronBounding(), CAULDRON),
    _CACTUS(new SimpleCollisionBox(0.0625, 0, 0.0625, 1 - 0.0625, 1 - 0.0625, 1 - 0.0625), CACTUS),


    _PISTON_BASE(new PistonBaseCollision(), MiscUtils.match("PISTON_BASE"), MiscUtils.match("PISTON_STICKY_BASE")),

    _PISTON_ARM(new PistonDickCollision(), MiscUtils.match("PISTON_EXTENSION")),

    _SOULSAND(new SimpleCollisionBox(0, 0, 0, 1, 0.875, 1),
            SOUL_SAND),
    _PICKLE((version, block) -> {
        SeaPickle pickle = (SeaPickle) block.getBlockData();

        switch(pickle.getPickles()) {
            case 1:
                return new SimpleCollisionBox(6.0D / 15, 0.0, 6.0D / 15,
                        10.0D / 15, 6.0D / 15, 10.0D / 15);
            case 2:
                return new SimpleCollisionBox(3.0D / 15, 0.0D, 3.0D / 15,
                        13.0D / 15, 6.0D / 15, 13.0D / 15);
            case 3:
                return new SimpleCollisionBox(2.0D / 15, 0.0D, 2.0D / 15,
                        14.0D / 15, 6.0D / 15, 14.0D / 15);
            case 4:
                return new SimpleCollisionBox(2.0D / 15, 0.0D, 2.0D / 15,
                        14.0D / 15, 7.0D / 15, 14.0D / 15);
        }
        return NoCollisionBox.INSTANCE;
    }, XMaterial.SEA_PICKLE.parseMaterial()),

    _NONE(NoCollisionBox.INSTANCE, LEVER, TORCH,
            MiscUtils.match("STONE_PLATE"),MiscUtils.match("GOLD_PLATE"),
            MiscUtils.match("IRON_PLATE"), MiscUtils.match("REDSTONE_TORCH_OFF"),
            MiscUtils.match("REDSTONE_TORCH_ON"), REDSTONE_WIRE,
            Material.matchMaterial("RAILS", true), POWERED_RAIL, ACTIVATOR_RAIL, DETECTOR_RAIL,
            STONE_BUTTON, MiscUtils.match("WOOD_BUTTON")),


    _DEFAULT(new SimpleCollisionBox(0, 0, 0, 1, 1, 1));

    private CollisionBox box;
    private CollisionFactory dynamic;
    private Material[] materials;

    BlockData(CollisionBox box, Material... materials) {
        this.box = box;
        Set<Material> mList = new HashSet<>();
        mList.addAll(Arrays.asList(materials));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Material[mList.size()]);
    }

    BlockData(CollisionFactory dynamic, Material... materials) {
        this.dynamic = dynamic;
        this.box = box;
        Set<Material> mList = new HashSet<>();
        mList.addAll(Arrays.asList(materials));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Material[mList.size()]);
    }

    public CollisionBox getBox(Block block, ProtocolVersion version) {
        if (this.box != null)
            return this.box.copy().offset(block.getX(), block.getY(), block.getZ());
        return new DynamicCollisionBox(dynamic, block, version).offset(block.getX(), block.getY(), block.getZ());
    }

    private static BlockData[] lookup = new BlockData[Material.values().length];

    static {
        for (BlockData data : values()) {
            for (Material mat : data.materials) lookup[mat.ordinal()] = data;
        }
    }

    public static BlockData getData(Material material) {
        BlockData data = lookup[material.ordinal()];
        return data != null ? data : _DEFAULT;
    }


}