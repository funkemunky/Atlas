package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.Materials;
import cc.funkemunky.api.utils.world.blocks.*;
import cc.funkemunky.api.utils.world.types.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Cake;
import org.bukkit.material.Directional;
import org.bukkit.material.Gate;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public enum BlockData {
    _VINE(NoCollisionBox.INSTANCE,
            106),

    _LIQUID(new SimpleCollisionBox(0, 0, 0, 1, 0.9, 1),
            Arrays.stream(Material.values())
                    .filter(mat -> Materials.checkFlag(mat, Materials.LIQUID))
                    .toArray(Material[]::new)),

    _BREWINGSTAND(new ComplexCollisionBox(
            new SimpleCollisionBox(0, 0, 0, 1, 0.125, 1),                      //base
            new SimpleCollisionBox(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625) //top
    ), 117),

    _ANVIL((protocol, b) -> {
        BlockState state = b.getState();
        b.setType(Material.getMaterial(145), true);
        int dir = state.getData().getData() & 0b01;
        CollisionBox box;
        if (dir == 1) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        } else {
            box = new SimpleCollisionBox(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        }
        return box;
    }, 145),

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
    }, 144),

    _DOOR(new DoorHandler(), Arrays.stream(Material.values())
            .filter(mat -> mat.name().contains("DOOR") && mat.isBlock() && !mat.name().contains("TRAP"))
            .toArray(Material[]::new)),

    _HOPPER(new HopperBounding(), 154),
    _CAKE((protocol, block) -> {
        Cake cake = (Cake) block.getType().getNewData(block.getData());

        double f1 = (1 + cake.getSlicesEaten() * 2) / 16D;

        return new SimpleCollisionBox(f1, 0, 0.0625, 1 - 0.0625, 0.5, 1 - 0.0625);
    }, 92),

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
    }, 65),

    _PLATE(new SimpleCollisionBox(0.125, 0, 0.125, 0.875, 0.25, 0.875),
            Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("_PLATE")).toArray(Material[]::new)),

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
    _PANE(new DynamicPane(), 102, 160, 101),
    _WALL(new DynamicWall(), 139),


    _SNOW((protocol, b) -> {
        MaterialData state = b.getState().getData();
        int height = (state.getData() & 0b1111);
        if (height == 0) return new SimpleCollisionBox(0, 0, 0, 1, 0, 1); // return NoCollisionBox.INSTANCE;
        return new SimpleCollisionBox(0, 0, 0, 1, height * 0.125, 1);
    }, 78),

    _SLAB((protocol, b) -> {
        MaterialData state = b.getState().getData();
        if ((state.getData() & 8) == 0)
            return new SimpleCollisionBox(0, 0, 0, 1, .5, 1);
        else return new SimpleCollisionBox(0, .5, 0, 1, 1, 1);
    }, Arrays.stream(Material.values()).filter(mat ->
            mat.name().contains("STEP") || mat.name().contains("SLAB"))
            .filter(mat -> !mat.name().contains("DOUBLE"))
            .toArray(Material[]::new)),

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

    _CHEST(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.125, 1)
            .expand(-0.125, 0, -0.125),
            54, 146, 130),
    _ETABLE(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.25, 1),
            116),
    _FRAME(new SimpleCollisionBox(0, 0, 0, 1, 1 - (0.0625 * 3), 1),
            120),

    _CARPET(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F),
            171),
    _Daylight(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.375, 1.0F),
            Arrays.stream(Material.values())
                    .filter(mat -> mat.name().contains("DAYLIGHT"))
                    .toArray(Material[]::new)),
    _LILIPAD((v, b) -> {
        if (v.isBelow(ProtocolVersion.V1_9))
            return new SimpleCollisionBox(0.0f, 0.0F, 0.0f, 1.0f, 0.015625F, 1.0f);
        return new SimpleCollisionBox(0.0625, 0.0F, 0.0625, 0.9375, 0.015625F, 0.9375);
    }, 111),

    _BED(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625, 1.0F),
            Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("_BED_BLOCK")
                    || mat.name().endsWith("_BED") || mat.name().startsWith("BED_"))
                    .toArray(Material[]::new)),


    _TRAPDOOR(new TrapDoorHandler(), Arrays.stream(Material.values())
            .filter(mat -> mat.name().contains("TRAP_DOOR")).toArray(Material[]::new)),

    _DIODE(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F),
            Arrays.stream(Material.values()).filter(mat -> mat.name().contains("DIODE")
                    || mat.name().contains("COMPARATOR")).toArray(Material[]::new)),
    _LECTERN((version, block) -> {
        Directional directional = (Directional) block.getType().getNewData(block.getData());
        ComplexCollisionBox box;
        switch(directional.getFacing()) {
            case NORTH:
                box = new ComplexCollisionBox(new SimpleCollisionBox(
                        0.0D, 10.0D / 16, 1.0D / 16,
                        16.0D / 16, 14.0D / 16, 5.333333 / 16),
                        new ComplexCollisionBox(
                                new SimpleCollisionBox(0.0D, 12.0D / 16, 5.333333D / 16,
                                        16.0D / 16, 16.0D / 16, 9.666667D / 16),
                                new SimpleCollisionBox(
                                        0.0D, 14.0D / 16, 9.666667D / 16,
                                        16.0D / 16, 18.0D / 16, 14.0D / 16)));
                break;
            case SOUTH:
                box = new ComplexCollisionBox(
                        new SimpleCollisionBox(0.0D, 10.0D / 16, 15.0D / 16,
                        16.0D / 16, 14.0D / 16, 10.666667D / 16),
                        new ComplexCollisionBox(
                                new SimpleCollisionBox(0.0D, 12.0D / 16, 10.666667D / 16,
                                16.0D / 16, 16.0D / 16, 6.333333D / 16),
                                new SimpleCollisionBox(0.0D, 14.0D / 16, 6.333333D / 16,
                                        16.0D / 16, 18.0D / 16, 2.0D / 16)));
                break;
            case EAST:
                box = new ComplexCollisionBox(
                        new SimpleCollisionBox(15.0D / 16, 10.0D / 16, 0.0D,
                        10.666667D / 16, 14.0D / 16, 16.0D / 16),
                        new ComplexCollisionBox(
                                new SimpleCollisionBox(10.666667D / 16, 12.0D / 16, 0.0D,
                                        6.333333D / 16, 16.0D / 16, 16.0D / 16),
                                new SimpleCollisionBox(6.333333D / 16, 14.0D / 16, 0.0D,
                                        2.0D / 16, 18.0D / 16, 16.0D / 16)));
                break;
            case WEST:
                box = new ComplexCollisionBox(
                        new SimpleCollisionBox(1.0D / 16, 10.0D / 16, 0.0D,
                                5.333333D / 16, 14.0D / 16, 16.0D / 16),
                        new ComplexCollisionBox(
                                new SimpleCollisionBox(5.333333D / 16, 12.0D / 16, 0.0D,
                                        9.666667D / 16, 16.0D / 16, 16.0D / 16),
                                new SimpleCollisionBox(9.666667D / 16, 14.0D / 16, 0.0D,
                                        14.0D / 16, 18.0D / 16, 16.0D / 16)));
                break;
            default:
                return new ComplexCollisionBox(
                        new SimpleCollisionBox(4.0D / 16, 2.0D / 16, 4.0 / 16D,
                                12.0D / 16, 14.0D / 16, 12.0D / 16),
                        new SimpleCollisionBox(0.0D, 0.0D, 0.0D,
                                16.0D / 16, 2.0D / 16, 16.0D / 16));
        }
        box.add(new ComplexCollisionBox(
                new SimpleCollisionBox(4.0D / 16, 2.0D / 16, 4.0 / 16D,
                        12.0D / 16, 14.0D / 16, 12.0D / 16),
                new SimpleCollisionBox(0.0D, 0.0D, 0.0D,
                        16.0D / 16, 2.0D / 16, 16.0D / 16)));
        return box;
    }, Material2.LECTERN),

    _STRUCTURE_VOID(new SimpleCollisionBox(0.375, 0.375, 0.375, 0.625, 0.625, 0.625),
            Material2.STRUCTURE_VOID), //
    _END_ROD(new DynamicRod(), Material2.END_ROD),
    _CAULDRON(new CouldronBounding(), 118),
    _CACTUS(new SimpleCollisionBox(0.0625, 0, 0.0625,
            1 - 0.0625, 1 - 0.0625, 1 - 0.0625), 81),


    _PISTON_BASE(new PistonBaseCollision(), 33, 29),

    _PISTON_ARM(new PistonDickCollision(), 34),

    _SOULSAND(new SimpleCollisionBox(0, 0, 0, 1, 0.875, 1),
            88),

    _NONE(NoCollisionBox.INSTANCE, 69),
    _NONE_ARRAY(NoCollisionBox.INSTANCE, Arrays.stream(Material.values()).filter(mat -> mat.name().contains("RAIL")
            || mat.name().contains("BUTTON") || mat.name().contains("TORCH")).toArray(Material[]::new)),


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

    BlockData(CollisionBox box) {
        this.box = box;
        this.materials = new Material[0];
    }


    BlockData(CollisionBox box, int... materials) {
        this.box = box;
        this.materials = Arrays.stream(materials)
                .mapToObj(Material::getMaterial)
                .filter(Objects::nonNull)
                .toArray(Material[]::new);
    }

    BlockData(CollisionFactory dynamic, Material... materials) {
        this.dynamic = dynamic;
        this.box = box;
        this.materials = Arrays.stream(materials)
                .filter(Objects::nonNull)
                .toArray(Material[]::new);
    }

    BlockData(CollisionFactory dynamic, int... materials) {
        this.dynamic = dynamic;
        this.box = box;
        this.materials = Arrays.stream(materials)
                .mapToObj(Material::getMaterial)
                .filter(Objects::nonNull)
                .toArray(Material[]::new);
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