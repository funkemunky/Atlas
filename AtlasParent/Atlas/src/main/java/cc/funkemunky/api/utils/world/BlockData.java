package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.Materials;
import cc.funkemunky.api.utils.MiscUtils;
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
import java.util.Objects;
import java.util.Set;

import static org.bukkit.Material.*;

public enum BlockData {
    _VINE(NoCollisionBox.INSTANCE,
            VINE),

    _LIQUID(new SimpleCollisionBox(0, 0, 0, 1, 0.9, 1),
            Arrays.stream(MiscUtils.array)
                    .filter(mat -> Materials.checkFlag(mat, Materials.LIQUID))
                    .toArray(Material[]::new)),

    _BREWINGSTAND(new ComplexCollisionBox(
            new SimpleCollisionBox(0, 0, 0, 1, 0.125, 1),                      //base
            new SimpleCollisionBox(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625) //top
    ), MiscUtils.getById(117)),

    _ANVIL((protocol, b) -> {
        BlockState state = b.getState();
        b.setType(MiscUtils.getById(145), true);
        int dir = state.getData().getData() & 0b01;
        CollisionBox box;
        if (dir == 1) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        } else {
            box = new SimpleCollisionBox(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        }
        return box;
    }, MiscUtils.getById(145)),

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
    }, MiscUtils.getById(144)),

    _DOOR(new DoorHandler(), Arrays.stream(MiscUtils.array)
            .filter(mat -> mat.name().endsWith("_DOOR"))
            .toArray(Material[]::new)),

    _HOPPER(new HopperBounding(), MiscUtils.getById(154)),
    _CAKE((protocol, block) -> {
        Cake cake = (Cake) block.getType().getNewData(block.getData());

        double f1 = (1 + cake.getSlicesEaten() * 2) / 16D;

        return new SimpleCollisionBox(f1, 0, 0.0625, 1 - 0.0625, 0.5, 1 - 0.0625);
    }, MiscUtils.getById(92)),

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
    }, MiscUtils.getById(65)),

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
    }, Arrays.stream(MiscUtils.array).filter(mat -> mat.name().endsWith("_GATE")).toArray(Material[]::new)),

    _FENCE(new DynamicFence(), MiscUtils.getById(85), MiscUtils.getById(113), MiscUtils.getById(188),
            MiscUtils.getById(189), MiscUtils.getById(190), MiscUtils.getById(191), MiscUtils.getById(192)),
    _PANE(new DynamicPane(), Arrays.stream(MiscUtils.array)
            .filter(mat -> mat.getId() == 101 || mat.getId() == 102 || mat.name().contains("PANE"))
            .toArray(Material[]::new)), //Checking for THIN_GLASS AND IRON_FENCE
    _WALL(new DynamicWall(), Arrays.stream(MiscUtils.array)
            .filter(mat -> mat.name().contains("WALL"))
            .toArray(Material[]::new)),

    _SNOW((protocol, b) -> {
        MaterialData state = b.getState().getData();
        int height = (state.getData() & 0b1111);
        if (height == 0) return new SimpleCollisionBox(0, 0, 0, 1, 0, 1); // return NoCollisionBox.INSTANCE;
        return new SimpleCollisionBox(0, 0, 0, 1, height * 0.125, 1);
    }, MiscUtils.getById(78)),

    _SLAB((protocol, b) -> {
        MaterialData state = b.getState().getData();
        if ((state.getData() & 8) == 0)
            return new SimpleCollisionBox(0, 0, 0, 1, .5, 1);
        else return new SimpleCollisionBox(0, .5, 0, 1, 1, 1);
    }, Arrays.stream(MiscUtils.array).filter(mat ->
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
    }, Arrays.stream(MiscUtils.array).filter(mat -> mat.name().contains("STAIRS")).toArray(Material[]::new)),

    _CHEST(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.125, 1)
            .expand(-0.125, 0, -0.125),
            MiscUtils.getById(54), MiscUtils.getById(146), MiscUtils.getById(130)),
    _ETABLE(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.25, 1),
            MiscUtils.getById(116)),
    _FRAME(new SimpleCollisionBox(0, 0, 0, 1, 1 - (0.0625 * 3), 1),
            MiscUtils.getById(120)),

    _CARPET(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F),
            MiscUtils.getById(171)),
    _Daylight(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.375, 1.0F),
            MiscUtils.getById(151), Material2.DAYLIGHT_DETECTOR_INVERTED),
    _LILIPAD((v, b) -> {
        if (v.isBelow(ProtocolVersion.V1_9))
            return new SimpleCollisionBox(0.0f, 0.0F, 0.0f, 1.0f, 0.015625F, 1.0f);
        return new SimpleCollisionBox(0.0625, 0.0F, 0.0625, 0.9375, 0.015625F, 0.9375);
    }, MiscUtils.getById(111)),

    _BED(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625, 1.0F),
            MiscUtils.getById(26)),


    _TRAPDOOR(new TrapDoorHandler(), MiscUtils.getById(96), MiscUtils.getById(167)),

    _LOGIC(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F),
            MiscUtils.getById(93), MiscUtils.getById(94), MiscUtils.getById(149), MiscUtils.getById(150)),

    _STRUCTURE_VOID(new SimpleCollisionBox(0.375, 0.375, 0.375, 0.625, 0.625, 0.625),
            Material2.STRUCTURE_VOID), //
    _END_ROD(new DynamicRod(), Material2.END_ROD),
    _CAULDRON(new CouldronBounding(), MiscUtils.getById(118)),
    _CACTUS(new SimpleCollisionBox(0.0625, 0, 0.0625,
            1 - 0.0625, 1 - 0.0625, 1 - 0.0625), MiscUtils.getById(81)),


    _PISTON_BASE(new PistonBaseCollision(), MiscUtils.getById(29), MiscUtils.getById(33)),

    _PISTON_ARM(new PistonDickCollision(), MiscUtils.getById(34)),

    _SOULSAND(new SimpleCollisionBox(0, 0, 0, 1, 0.875, 1),
            MiscUtils.getById(88)),

    _NONE(NoCollisionBox.INSTANCE, MiscUtils.getById(69), MiscUtils.getById(50), MiscUtils.getById(72), MiscUtils.getById(70), MiscUtils.getById(147), MiscUtils.getById(148), MiscUtils.getById(75),
            MiscUtils.getById(76), MiscUtils.getById(55), MiscUtils.getById(66), MiscUtils.getById(27), MiscUtils.getById(157), MiscUtils.getById(28),
            MiscUtils.getById(77), MiscUtils.getById(143)),


    _DEFAULT(new SimpleCollisionBox(0, 0, 0, 1, 1, 1));


    private CollisionBox box;
    private CollisionFactory dynamic;
    private Material[] materials;

    BlockData(CollisionBox box, Material... materials) {
        this.box = box;
        Set<Material> mList = new HashSet<>(Arrays.asList(materials));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Material[0]);
    }

    BlockData(CollisionFactory dynamic, Material... materials) {
        this.dynamic = dynamic;
        Set<Material> mList = new HashSet<>(Arrays.asList(materials));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Material[0]);
    }

    public CollisionBox getBox(Block block, ProtocolVersion version) {
        if (this.box != null)
            return this.box.copy().offset(block.getX(), block.getY(), block.getZ());
        return new DynamicCollisionBox(dynamic, block, version).offset(block.getX(), block.getY(), block.getZ());
    }

    private static BlockData[] lookup = new BlockData[2276];
    static {
        for (BlockData data : values()) {
            for (Material mat : data.materials) lookup[mat.getId()] = data;
        }
    }

    public static BlockData getData(Material material) {
        if(material.getId() > lookup.length - 1) return BlockData._DEFAULT;
        BlockData data = lookup[material.getId() - 1];

        if(data != null) {
            return data;
        }
        return BlockData._DEFAULT;
    }
}