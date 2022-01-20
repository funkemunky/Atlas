package cc.funkemunky.api.utils.world.blocks;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.state.BlockStateManager;
import cc.funkemunky.api.utils.world.types.CollisionFactory;
import cc.funkemunky.api.utils.world.types.ComplexCollisionBox;
import cc.funkemunky.api.utils.world.types.NoCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class DynamicLectern implements CollisionFactory {
    @Override
    public CollisionBox fetch(ProtocolVersion version, Block block) {
         /*   b = BlockProperties.w;
        c = BlockProperties.o;
        d = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
        e = Block.a(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
        f = VoxelShapes.a(d, e);
        g = Block.a(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
        h = VoxelShapes.a(f, g);
        i = VoxelShapes.a(Block.a(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), new VoxelShape[]{Block.a(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.a(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), f});
        j = VoxelShapes.a(Block.a(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), new VoxelShape[]{Block.a(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.a(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), f});
        k = VoxelShapes.a(Block.a(15.0D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D), new VoxelShape[]{Block.a(10.666667D, 12.0D, 0.0D, 6.333333D, 16.0D, 16.0D), Block.a(6.333333D, 14.0D, 0.0D, 2.0D, 18.0D, 16.0D), f});
        w = VoxelShapes.a(Block.a(0.0D, 10.0D, 15.0D, 16.0D, 14.0D, 10.666667D), new VoxelShape[]{Block.a(0.0D, 12.0D, 10.666667D, 16.0D, 16.0D, 6.333333D), Block.a(0.0D, 14.0D, 6.333333D, 16.0D, 18.0D, 2.0D), f});
        */
            /*  public VoxelShape a(IBlockData var0, IBlockAccess var1, BlockPosition var2, VoxelShapeCollision var3) {
        switch((EnumDirection)var0.get(a)) {
        case NORTH:
            return j;
        case SOUTH:
            return w;
        case EAST:
            return k;
        case WEST:
            return i;
        default:
            return f;
        }
    }
*/
        BlockFace facing = (BlockFace) BlockStateManager.getInterface("facing", block);

        if(facing == null) return NoCollisionBox.INSTANCE;

        switch(facing) {
            case NORTH: {
                return j.copy();
            }
            case SOUTH: {
                return w.copy();
            }
            case EAST: {
                return k.copy();
            }
            case WEST: {
                return i.copy();
            }
            default: {
                return f.copy();
            }
        }
    }

    private static final ComplexCollisionBox f = new ComplexCollisionBox(
            new SimpleCollisionBox(0, 0, 0, 1, 0.125, 1),
            new SimpleCollisionBox(0.25, 0.125, 0.25, 0.75, 0.875, 0.75)),
            j = new ComplexCollisionBox(
                    new SimpleCollisionBox(0, 0.625, 0.0625,
                            1, 0.875, 0.333333312),
                    new SimpleCollisionBox(0, 0.75, 0.333333312,
                            1, 1, 0.6041666875),
                    new SimpleCollisionBox(0, 0.875, 0.6041666875,
                            1, 0.888888889, 0.875),
                    f),
            w = new ComplexCollisionBox(
                    new SimpleCollisionBox(0, 0.625, 0.9375,
                            1, 0.875, .6666666875),
                    new SimpleCollisionBox(0, 0.75, .6666666875,
                            1, 1, 0.3958333125),
                    new SimpleCollisionBox(0, 0.875, 0.3958333125,
                            1, 1.125, 0.125),
                  f),
            k = new ComplexCollisionBox(
                    new SimpleCollisionBox(0.9375, 0.625, 0,
                            .6666666875, 0.875, 1),
                    new SimpleCollisionBox(.6666666875, 0.75, 0, 0.333333312, 1, 1),
                    new SimpleCollisionBox(0.333333312, 0.875, 0, 0.125, 1.125, 1),
                    f),
            i = new ComplexCollisionBox(
                    new SimpleCollisionBox(0.0625, 0.625, 0, 0.3333333125, 0.875, 1),
                    new SimpleCollisionBox(0.333333312, 0.75, 0, 0.6041666875, 1,1),
                    new SimpleCollisionBox(0.6041666875, 0.875, 0, 0.875, 1.125, 1),
                    f);
}
