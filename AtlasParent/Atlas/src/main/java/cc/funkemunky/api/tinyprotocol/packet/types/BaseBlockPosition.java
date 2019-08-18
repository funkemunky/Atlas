package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.Reflections;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;

public class BaseBlockPosition extends NMSObject {
    public static final BaseBlockPosition ZERO = new BaseBlockPosition(0, 0, 0);
    private static FieldAccessor<Integer> fieldX = fetchField(Type.BASEBLOCKPOSITION, int.class, 0);
    private static FieldAccessor<Integer> fieldY = fetchField(Type.BASEBLOCKPOSITION, int.class, 1);
    private static FieldAccessor<Integer> fieldZ = fetchField(Type.BASEBLOCKPOSITION, int.class, 2);
    private static WrappedClass baseBlockPositionClass = Reflections.getNMSClass("BaseBlockPosition");
    private static WrappedClass blockPositionClass = Reflections.getNMSClass("BlocKPosition");
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
        double var7 = (double) this.getX() - var1;
        double var9 = (double) this.getY() - var3;
        double var11 = (double) this.getZ() - var5;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    public double d(double var1, double var3, double var5) {
        double var7 = (double) this.getX() + 0.5D - var1;
        double var9 = (double) this.getY() + 0.5D - var3;
        double var11 = (double) this.getZ() + 0.5D - var5;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    public double i(BaseBlockPosition var1) {
        return this.c((double) var1.getX(), (double) var1.getY(), (double) var1.getZ());
    }

    public Object getAsBaseBlockPosition() {
        return baseBlockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(getX(), getY(), getZ());
    }

    public Object getAsBlockPosition() {
        return blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(getX(), getY(), getZ());
    }
}
