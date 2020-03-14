package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;

public class Vec3D extends NMSObject {
    private static FieldAccessor<Double> fieldX = fetchField("Vec3D", double.class, 0);
    private static FieldAccessor<Double> fieldY = fetchField("Vec3D", double.class, 1);
    private static FieldAccessor<Double> fieldZ = fetchField("Vec3D", double.class, 2);
    private static WrappedConstructor vec3dConst = MinecraftReflection.vec3D
            .getConstructor(double.class, double.class, double.class);
    public final double a;
    public final double b;
    public final double c;

    public Vec3D(Object obj) {
        setObject(obj);
        this.a = fetch(fieldX);
        this.b = fetch(fieldY);
        this.c = fetch(fieldZ);
    }

    public Vec3D(double var1, double var3, double var5) {
        if (var1 == -0.0D) {
            var1 = 0.0D;
        }

        if (var3 == -0.0D) {
            var3 = 0.0D;
        }

        if (var5 == -0.0D) {
            var5 = 0.0D;
        }

        this.a = var1;
        this.b = var3;
        this.c = var5;

        setObject(vec3dConst.newInstance(a, b, c));
    }

    public Vec3D(BaseBlockPosition var1) {
        this(var1.getX(),  var1.getY(),  var1.getZ());
    }

    public Vec3D a() {
        double var1 =  MathHelper.sqrt(this.a * this.a + this.b * this.b + this.c * this.c);
        return var1 < 1.0E-4D ? new Vec3D(0.0D, 0.0D, 0.0D) : new Vec3D(this.a / var1, this.b / var1, this.c / var1);
    }

    public double b(Vec3D var1) {
        return this.a * var1.a + this.b * var1.b + this.c * var1.c;
    }

    public Vec3D d(Vec3D var1) {
        return this.a(var1.a, var1.b, var1.c);
    }

    public Vec3D a(double var1, double var3, double var5) {
        return this.add(-var1, -var3, -var5);
    }

    public Vec3D e(Vec3D var1) {
        return this.add(var1.a, var1.b, var1.c);
    }

    public Vec3D add(double var1, double var3, double var5) {
        return new Vec3D(this.a + var1, this.b + var3, this.c + var5);
    }

    public double distanceSquared(Vec3D var1) {
        double var2 = var1.a - this.a;
        double var4 = var1.b - this.b;
        double var6 = var1.c - this.c;
        return var2 * var2 + var4 * var4 + var6 * var6;
    }

    public double b() {
        return  MathHelper.sqrt(this.a * this.a + this.b * this.b + this.c * this.c);
    }

    public Vec3D a(Vec3D var1, double var2) {
        double var4 = var1.a - this.a;
        double var6 = var1.b - this.b;
        double var8 = var1.c - this.c;
        if (var4 * var4 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double var10 = (var2 - this.a) / var4;
            return var10 >= 0.0D && var10 <= 1.0D ? new Vec3D(this.a + var4 * var10, this.b + var6 * var10, this.c + var8 * var10) : null;
        }
    }

    public Vec3D b(Vec3D var1, double var2) {
        double var4 = var1.a - this.a;
        double var6 = var1.b - this.b;
        double var8 = var1.c - this.c;
        if (var6 * var6 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double var10 = (var2 - this.b) / var6;
            return var10 >= 0.0D && var10 <= 1.0D ? new Vec3D(this.a + var4 * var10, this.b + var6 * var10, this.c + var8 * var10) : null;
        }
    }

    public Vec3D c(Vec3D var1, double var2) {
        double var4 = var1.a - this.a;
        double var6 = var1.b - this.b;
        double var8 = var1.c - this.c;
        if (var8 * var8 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double var10 = (var2 - this.c) / var8;
            return var10 >= 0.0D && var10 <= 1.0D ? new Vec3D(this.a + var4 * var10, this.b + var6 * var10, this.c + var8 * var10) : null;
        }
    }

    public String toString() {
        return "(" + this.a + ", " + this.b + ", " + this.c + ")";
    }

    public Vec3D a(float var1) {
        float var2 = MathHelper.cos(var1);
        float var3 = MathHelper.sin(var1);
        double var4 = this.a;
        double var6 = this.b *  var2 + this.c *  var3;
        double var8 = this.c *  var2 - this.b *  var3;
        return new Vec3D(var4, var6, var8);
    }

    public Vec3D b(float var1) {
        float var2 = MathHelper.cos(var1);
        float var3 = MathHelper.sin(var1);
        double var4 = this.a *  var2 + this.c *  var3;
        double var6 = this.b;
        double var8 = this.c *  var2 - this.a *  var3;
        return new Vec3D(var4, var6, var8);
    }

    @Override
    public void updateObject() {
        fieldX.set(getObject(), a);
        fieldY.set(getObject(), b);
        fieldZ.set(getObject(), c);
    }
}
