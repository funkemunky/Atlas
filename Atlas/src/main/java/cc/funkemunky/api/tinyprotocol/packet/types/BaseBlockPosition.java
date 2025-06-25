package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;

public class BaseBlockPosition extends NMSObject {
    public static final BaseBlockPosition ZERO = new BaseBlockPosition(0, 0, 0);
    private static final FieldAccessor<Integer> fieldX, fieldY, fieldZ;
    private static final WrappedClass blockPositionClass, baseBlockPositionClass;
    private static final WrappedConstructor blockPosConstructor, baseBlockPosConstructor;
    private int a;
    private int c;
    private int d;
    private static final int NUM_X_BITS = 1 + cc.funkemunky.api.utils.MathHelper
            .calculateLogBaseTwo(cc.funkemunky.api.utils.MathHelper.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public BaseBlockPosition(Object obj) {
        setObject(obj);
        this.a = fetch(fieldX);
        this.c = fetch(fieldY);
        this.d = fetch(fieldZ);
    }

    public BaseBlockPosition(long serialized) {
        a = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        c = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        d = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
    }

    public BaseBlockPosition(int var1, int var2, int var3) {
        this.a = var1;
        this.c = var2;
        this.d = var3;
    }

    public BaseBlockPosition(double var1, double var3, double var5) {
        this(MathHelper.floor(var1), MathHelper.floor(var3), MathHelper.floor(var5));
    }

    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof BaseBlockPosition)) {
            return false;
        } else {
            BaseBlockPosition var2 = (BaseBlockPosition) var1;
            if (this.getX() != var2.getX()) {
                return false;
            } else if (this.getY() != var2.getY()) {
                return false;
            } else {
                return this.getZ() == var2.getZ();
            }
        }
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int g(BaseBlockPosition var1) {
        if (this.getY() == var1.getY()) {
            return this.getZ() == var1.getZ() ? this.getX() - var1.getX() : this.getZ() - var1.getZ();
        } else {
            return this.getY() - var1.getY();
        }
    }

    public int getX() {
        return this.a;
    }

    public int getY() {
        return this.c;
    }

    public int getZ() {
        return this.d;
    }

    public void setX(int x) {
        this.a = x;
    }

    public void setY(int y) {
        this.c = y;
    }

    public void setZ(int z) {
        this.d = z;
    }

    public BaseBlockPosition d(BaseBlockPosition var1) {
        return new BaseBlockPosition(this.getY() * var1.getZ() - this.getZ() * var1.getY(), this.getZ() * var1.getX() - this.getX() * var1.getZ(), this.getX() * var1.getY() - this.getY() * var1.getX());
    }

    public long toLong()
    {
        return ((long)this.getX() & X_MASK) << X_SHIFT
                | ((long)this.getY() & Y_MASK) << Y_SHIFT
                | ((long) this.getZ() & Z_MASK);
    }

    public double c(double var1, double var3, double var5) {
        double var7 = this.getX() - var1;
        double var9 = this.getY() - var3;
        double var11 = this.getZ() - var5;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    public double d(double var1, double var3, double var5) {
        double var7 = this.getX() + 0.5D - var1;
        double var9 = this.getY() + 0.5D - var3;
        double var11 = this.getZ() + 0.5D - var5;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    public double i(BaseBlockPosition var1) {
        return this.c(var1.getX(), var1.getY(), var1.getZ());
    }

    public <T> T getAsBaseBlockPosition() {
        assert ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8)
                : "Plugin is trying to access BaseBlockPosition on a 1.7.10 server";

        return baseBlockPosConstructor.newInstance(getX(), getY(), getZ());
    }

    public <T> T getAsBlockPosition() {
        return blockPosConstructor.newInstance(getX(), getY(), getZ());
    }

    @Override
    public void updateObject() {

    }

    static {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
            fieldX = fetchField(Type.BASEBLOCKPOSITION, int.class, 0);
            fieldY = fetchField(Type.BASEBLOCKPOSITION, int.class, 1);
            fieldZ = fetchField(Type.BASEBLOCKPOSITION, int.class, 2);
            blockPositionClass = Reflections.getNMSClass(Type.BLOCKPOSITION);
            blockPosConstructor = blockPositionClass.getConstructor(int.class, int.class, int.class);
            baseBlockPositionClass = Reflections.getNMSClass(Type.BASEBLOCKPOSITION);
            baseBlockPosConstructor = baseBlockPositionClass.getConstructor(int.class, int.class, int.class);
        } else {
            fieldX = fetchField(Type.CHUNKPOSITION, int.class, 0);
            fieldY = fetchField(Type.CHUNKPOSITION, int.class, 1);
            fieldZ = fetchField(Type.CHUNKPOSITION, int.class, 2);
            blockPositionClass = Reflections.getNMSClass(Type.CHUNKPOSITION);
            blockPosConstructor = blockPositionClass.getConstructor(int.class, int.class, int.class);
            baseBlockPositionClass = null;
            baseBlockPosConstructor = null;
        }
    }
}
