package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.world.blocks.*;
import cc.funkemunky.api.utils.world.types.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
            WATER, LAVA, STATIONARY_LAVA, STATIONARY_WATER),

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
    }, SKULL),

    _DOOR(new DoorHandler(), WOODEN_DOOR, ACACIA_DOOR, BIRCH_DOOR, JUNGLE_DOOR, IRON_DOOR, DARK_OAK_DOOR, SPRUCE_DOOR, IRON_DOOR_BLOCK),

    _HOPPER(new HopperBounding(), HOPPER),
    _CAKE((protocol, block) -> {
        Cake cake = (Cake) block.getType().getNewData(block.getData());

        double f1 = (1 + cake.getSlicesEaten() * 2) / 16D;

        return new SimpleCollisionBox(f1, 0, 0.0625, 1 - 0.0625, 0.5, 1 - 0.0625);
    }, CAKE, CAKE_BLOCK),

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
    }, FENCE_GATE, ACACIA_FENCE_GATE, BIRCH_FENCE_GATE, DARK_OAK_FENCE_GATE, JUNGLE_FENCE_GATE, SPRUCE_FENCE_GATE),

    _FENCE(new DynamicFence(), ACACIA_FENCE, BIRCH_FENCE, DARK_OAK_FENCE, JUNGLE_FENCE, FENCE, NETHER_FENCE, SPRUCE_FENCE),
    _PANE(new DynamicPane(), THIN_GLASS, STAINED_GLASS_PANE, IRON_FENCE),
    _WALL(new DynamicWall(), COBBLE_WALL),


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
    }, Material.STONE_SLAB2, Material.STEP, Material2.PURPUR_SLAB, Material.WOOD_STEP),

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
    }, ACACIA_STAIRS, BIRCH_WOOD_STAIRS, DARK_OAK_STAIRS, JUNGLE_WOOD_STAIRS, WOOD_STAIRS, SPRUCE_WOOD_STAIRS,
            BRICK_STAIRS, COBBLESTONE_STAIRS, SMOOTH_STAIRS, RED_SANDSTONE_STAIRS, SANDSTONE_STAIRS, QUARTZ_STAIRS,
            Material2.PURPUR_STAIRS),

    _CHEST(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.125, 1).expand(-0.125, 0, -0.125),
            CHEST, TRAPPED_CHEST, ENDER_CHEST),
    _ETABLE(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.25, 1),
            ENCHANTMENT_TABLE),
    _FRAME(new SimpleCollisionBox(0, 0, 0, 1, 1 - (0.0625 * 3), 1),
            ENDER_PORTAL_FRAME),

    _CARPET(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F), CARPET),
    _Daylight(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.375, 1.0F), DAYLIGHT_DETECTOR, Material2.DAYLIGHT_DETECTOR_INVERTED),
    _LILIPAD((v, b) -> {
        if (v.isBelow(ProtocolVersion.V1_9))
            return new SimpleCollisionBox(0.0f, 0.0F, 0.0f, 1.0f, 0.015625F, 1.0f);
        return new SimpleCollisionBox(0.0625, 0.0F, 0.0625, 0.9375, 0.015625F, 0.9375);
    }, WATER_LILY),

    _BED(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625, 1.0F), BED_BLOCK),


    _TRAPDOOR(new TrapDoorHandler(), IRON_TRAPDOOR, TRAP_DOOR),

    _STUPID(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F),
            DIODE_BLOCK_OFF, DIODE_BLOCK_ON, REDSTONE_COMPARATOR_ON, REDSTONE_COMPARATOR_OFF),

    _STRUCTURE_VOID(new SimpleCollisionBox(0.375, 0.375, 0.375, 0.625, 0.625, 0.625), Material2.STRUCTURE_VOID), //
    _END_ROD(new DynamicRod(), Material2.END_ROD),
    _CAULDRON(new CouldronBounding(), CAULDRON),
    _CACTUS(new SimpleCollisionBox(0.0625, 0, 0.0625, 1 - 0.0625, 1 - 0.0625, 1 - 0.0625), CACTUS),


    _PISTON_BASE(new PistonBaseCollision(), Material.PISTON_BASE, Material.PISTON_STICKY_BASE),

    _PISTON_ARM(new PistonDickCollision(), Material.PISTON_EXTENSION),

    _SOULSAND(new SimpleCollisionBox(0, 0, 0, 1, 0.875, 1),
            SOUL_SAND),

    _NONE(NoCollisionBox.INSTANCE, LEVER, TORCH, WOOD_PLATE, STONE_PLATE, GOLD_PLATE, IRON_PLATE, REDSTONE_TORCH_OFF,
            REDSTONE_TORCH_ON, REDSTONE_WIRE, RAILS, POWERED_RAIL, ACTIVATOR_RAIL, DETECTOR_RAIL,
            STONE_BUTTON, WOOD_BUTTON),


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