package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.XMaterial;
import cc.funkemunky.api.utils.world.blocks.*;
import cc.funkemunky.api.utils.world.types.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.material.Cake;
import org.bukkit.material.Gate;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Vine;

import java.util.*;
import java.util.stream.Stream;

public enum BlockData {
    _VINE((v, block) -> {
        Vine data = (Vine) block.getType().getNewData(block.getData());

        if(data.isOnFace(BlockFace.UP))
            return new SimpleCollisionBox(0., 0.9375, 0.,
                    1., 1., 1.);

        if(data.isOnFace(BlockFace.NORTH))
            return new SimpleCollisionBox(0., 0., 0.,
                    1., 1., 0.0625);

        if(data.isOnFace(BlockFace.EAST))
            return new SimpleCollisionBox(0.9375, 0., 0.,
                    1., 1., 1.);

        if(data.isOnFace(BlockFace.SOUTH))
            return new SimpleCollisionBox(0., 0., 0.9375,
                    1., 1., 1.);

        if(data.isOnFace(BlockFace.WEST))
            return new SimpleCollisionBox(0., 0., 0.,
                    0.0625, 1., 1.);

        return new SimpleCollisionBox(0,0,0,1.,1.,1.);
    }, XMaterial.VINE.parseMaterial()),

    _LIQUID(new SimpleCollisionBox(0, 0, 0, 1, 0.9, 1),
            XMaterial.WATER.parseMaterial(), XMaterial.LAVA.parseMaterial(),
            MiscUtils.match("STATIONARY_LAVA"), MiscUtils.match("STATIONARY_WATER")),

    _BREWINGSTAND(new ComplexCollisionBox(
            new SimpleCollisionBox(0, 0, 0, 1, 0.125, 1),                      //base
            new SimpleCollisionBox(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625) //top
    ), XMaterial.BREWING_STAND.parseMaterial()),

    _ANVIL((protocol, b) -> {
        BlockState state = b.getState();
        b.setType(XMaterial.ANVIL.parseMaterial(), true);
        int dir = state.getData().getData() & 0b01;
        CollisionBox box;
        if (dir == 1) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        } else {
            box = new SimpleCollisionBox(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        }
        return box;
    }, XMaterial.ANVIL.parseMaterial()),

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

    _HOPPER(new HopperBounding(), XMaterial.HOPPER.parseMaterial()),
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
    }, XMaterial.LADDER.parseMaterial()),

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
    }, Arrays.stream(XMaterial.values()).filter(mat -> mat.name().contains("FENCE") && mat.name().contains("GATE"))
            .map(XMaterial::parseMaterial)
            .toArray(Material[]::new)),

    _FENCE(new DynamicFence(), Arrays.stream(XMaterial.values())
            .filter(mat -> mat.name().equals("FENCE") || mat.name().endsWith("FENCE"))
            .map(BlockData::m)
            .toArray(Material[]::new)),
    _PANE(new DynamicPane(), MiscUtils.match("THIN_GLASS"), MiscUtils.match("STAINED_GLASS_PANE"),
            MiscUtils.match("IRON_FENCE")),
    _WALL(new DynamicWall(), Arrays.stream(XMaterial.values())
            .filter(mat -> mat.name().contains("WALL"))
            .map(BlockData::m)
            .toArray(Material[]::new)),


    _SNOW((protocol, b) -> {
        MaterialData state = b.getState().getData();
        int height = (state.getData() & 0b1111);
        if (height == 0) return new SimpleCollisionBox(0, 0, 0, 1, 0, 1); // return NoCollisionBox.INSTANCE;
        return new SimpleCollisionBox(0, 0, 0, 1, height * 0.125, 1);
    }, XMaterial.SNOW.parseMaterial()),

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
    }, Arrays.stream(XMaterial.values()).filter(mat -> mat.name().contains("STAIRS"))
            .map(BlockData::m)
            .toArray(Material[]::new)),

    _CHEST(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.125, 1).expand(-0.125, 0, -0.125),
            XMaterial.CHEST.parseMaterial(), 
            XMaterial.TRAPPED_CHEST.parseMaterial(), 
            XMaterial.ENDER_CHEST.parseMaterial()),
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
            Arrays.stream(XMaterial.values()).filter(mat -> mat.name().contains("BED"))
                    .map(BlockData::m)
                    .toArray(Material[]::new)),


    _TRAPDOOR(new TrapDoorHandler(), Arrays.stream(Material.values())
            .filter(mat -> mat.name().contains("TRAP_DOOR")).toArray(Material[]::new)),

    _STUPID(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F),
            MiscUtils.match("DIODE_BLOCK_OFF"), MiscUtils.match("DIODE_BLOCK_ON"),
            MiscUtils.match("REDSTONE_COMPARATOR_ON"), MiscUtils.match("REDSTONE_COMPARATOR_OFF")),

    _STRUCTURE_VOID(new SimpleCollisionBox(0.375, 0.375, 0.375, 
            0.625, 0.625, 0.625),
            XMaterial.STRUCTURE_VOID.parseMaterial()),
    
    _END_ROD(new DynamicRod(), XMaterial.END_ROD.parseMaterial()),
    _CAULDRON(new CouldronBounding(), XMaterial.CAULDRON.parseMaterial()),
    _CACTUS(new SimpleCollisionBox(0.0625, 0, 0.0625, 
            1 - 0.0625, 1 - 0.0625, 1 - 0.0625), XMaterial.CACTUS.parseMaterial()),


    _PISTON_BASE(new PistonBaseCollision(), m(XMaterial.PISTON), m(XMaterial.STICKY_PISTON)),

    _PISTON_ARM(new PistonDickCollision(), m(XMaterial.PISTON_HEAD)),

    _SOULSAND(new SimpleCollisionBox(0, 0, 0, 1, 0.875, 1),
            XMaterial.SOUL_SAND.parseMaterial()),
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

    _NONE(NoCollisionBox.INSTANCE, Stream.of(XMaterial.LEVER, XMaterial.TORCH, XMaterial.REDSTONE_TORCH,
            XMaterial.REDSTONE_WIRE, XMaterial.REDSTONE_WALL_TORCH, XMaterial.POWERED_RAIL,
            XMaterial.RAIL, XMaterial.ACTIVATOR_RAIL, XMaterial.DETECTOR_RAIL, XMaterial.AIR, XMaterial.GRASS)
            .map(BlockData::m)
            .toArray(Material[]::new)),

    _NONE2(NoCollisionBox.INSTANCE, Arrays.stream(XMaterial.values())
            .filter(mat -> {
                List<String> names = new ArrayList<>(Arrays.asList(mat.names));
                names.add(mat.name());
                return names.stream().anyMatch(name ->
                        name.contains("PLATE") || name.contains("BUTTON"));
            }).map(BlockData::m).toArray(Material[]::new)),
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
        Material matched = MiscUtils.match(material.toString());
        BlockData data = lookup[matched.ordinal()];
        return data != null ? data : _DEFAULT;
    }

    private static Material m(XMaterial xmat) {
        return xmat.parseMaterial();
    }
}