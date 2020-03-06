package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;

public class BaseBlockPosition extends NMSObject {
    public static final BaseBlockPosition ZERO = new BaseBlockPosition(0, 0, 0);
    private static FieldAccessor<Integer> fieldX;
    private static FieldAccessor<Integer> fieldY;
    private static FieldAccessor<Integer> fieldZ;
    private static WrappedClass baseBlockPositionClass;
    private static WrappedClass blockPositionClass;
    private static WrappedConstructor blockPosConstructor;
    private static WrappedConstructor baseBlockPosConstructor;
    private int a;
    private int c;
    private int d;

    public BaseBlockPosition(Object obj) {
        setObject(obj);
        this.a = fetch(fieldX);
        this.c = fetch(fieldY);
        this.d = fetch(fieldZ);
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

    public BaseBlockPosition d(BaseBlockPosition var1) {
        return new BaseBlockPosition(this.getY() * var1.getZ() - this.getZ() * var1.getY(), this.getZ() * var1.getX() - this.getX() * var1.getZ(), this.getX() * var1.getY() - this.getY() * var1.getX());
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
            baseBlockPositionClass = Reflections.getNMSClass("BaseBlockPosition");
            blockPositionClass = Reflections.getNMSClass("BlockPosition");
            blockPosConstructor = blockPositionClass.getConstructor(int.class, int.class, int.class);
            baseBlockPosConstructor = baseBlockPositionClass.getConstructor(int.class, int.class, int.class);
        }
    }
}
