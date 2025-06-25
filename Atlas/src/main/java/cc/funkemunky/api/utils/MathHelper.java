package cc.funkemunky.api.utils;


import java.util.Random;
import java.util.UUID;

public class MathHelper {
    public static final float SQRT_2 = sqrt_float(2.0F);
    public static final float PI = (float) Math.PI;
    public static final float PI2 = ((float) Math.PI * 2F);
    public static final float PId2 = ((float) Math.PI / 2F);
    public static final float deg2Rad = 0.017453292F;
    private static final int SIN_BITS = 12;
    private static final int SIN_MASK = 4095;
    private static final int SIN_COUNT = 4096;
    private static final float radFull = ((float) Math.PI * 2F);
    private static final float degFull = 360.0F;
    private static final float radToIndex = 651.8986F;
    private static final float degToIndex = 11.377778F;
    private static final float[] SIN_TABLE_FAST = new float[4096];
    /**
     * A table of sin values computed from 0 (inclusive) to 2*pi (exclusive), with steps of 2*PI / 65536.
     */
    private static final float[] SIN_TABLE = new float[65536];
    /**
     * Though it looks like an array, this is really more like a mapping.  Key (index of this array) is the upper 5 bits
     * of the result of multiplying a 32-bit unsigned integer by the B(2, 5) De Bruijn sequence 0x077CB531.  Value
     * (value stored in the array) is the unique index (from the right) of the leftmost one-bit in a 32-bit unsigned
     * integer that can cause the upper 5 bits to get that value.  Used for highly optimized "find the log-base-2 of
     * this number" calculations.
     */
    private static final int[] multiplyDeBruijnBitPosition;
    private static final double field_181163_d;
    private static final double[] field_181164_e;
    private static final double[] field_181165_f;
    private static final String __OBFID = "CL_00001496";
    public static boolean fastMath = false;

    static {
        for (int i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
        }

        for (int j = 0; j < 4096; ++j) {
            SIN_TABLE_FAST[j] = (float) Math.sin((double) (((float) j + 0.5F) / 4096.0F * ((float) Math.PI * 2F)));
        }

        for (int l = 0; l < 360; l += 90) {
            SIN_TABLE_FAST[(int) ((float) l * 11.377778F) & 4095] = (float) Math.sin((double) ((float) l * 0.017453292F));
        }

        multiplyDeBruijnBitPosition = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
        field_181163_d = Double.longBitsToDouble(4805340802404319232L);
        field_181164_e = new double[257];
        field_181165_f = new double[257];

        for (int k = 0; k < 257; ++k) {
            double d1 = (double) k / 256.0D;
            double d0 = Math.asin(d1);
            field_181165_f[k] = Math.cos(d0);
            field_181164_e[k] = d0;
        }
    }

    /**
     * sin looked up in a table
     */
    public static float sin(float p_76126_0_) {
        return fastMath ? SIN_TABLE_FAST[(int) (p_76126_0_ * 651.8986F) & 4095] : SIN_TABLE[(int) (p_76126_0_ * 10430.378F) & 65535];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static float cos(float value) {
        return fastMath ? SIN_TABLE_FAST[(int) ((value + ((float) Math.PI / 2F)) * 651.8986F) & 4095] : SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
    }

    public static float sqrt_float(float value) {
        return (float) Math.sqrt((double) value);
    }

    public static float sqrt_double(double value) {
        return (float) Math.sqrt(value);
    }

    /**
     * Returns the greatest integer less than or equal to the float argument
     */
    public static int floor_float(float value) {
        int i = (int) value;
        return value < (float) i ? i - 1 : i;
    }

    /**
     * returns par0 cast as an int, and no greater than Integer.MAX_VALUE-1024
     */
    public static int truncateDoubleToInt(double value) {
        return (int) (value + 1024.0D) - 1024;
    }

    /**
     * Returns the greatest integer less than or equal to the double argument
     */
    public static int floor_double(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    /**
     * Long version of floor_double
     */
    public static long floor_double_long(double value) {
        long i = (long) value;
        return value < (double) i ? i - 1L : i;
    }

    public static int func_154353_e(double value) {
        return (int) (value >= 0.0D ? value : -value + 1.0D);
    }

    public static float abs(float value) {
        return value >= 0.0F ? value : -value;
    }

    /**
     * Returns the unsigned value of an int.
     */
    public static int abs_int(int value) {
        return value >= 0 ? value : -value;
    }

    public static int ceiling_float_int(float value) {
        int i = (int) value;
        return value > (float) i ? i + 1 : i;
    }

    public static int ceiling_double_int(double value) {
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters.
     */
    public static int clamp_int(int num, int min, int max) {
        return num < min ? min : (num > max ? max : num);
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters
     */
    public static float clamp_float(float num, float min, float max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static double clamp_double(double num, double min, double max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static double denormalizeClamp(double p_151238_0_, double p_151238_2_, double p_151238_4_) {
        return p_151238_4_ < 0.0D ? p_151238_0_ : (p_151238_4_ > 1.0D ? p_151238_2_ : p_151238_0_ + (p_151238_2_ - p_151238_0_) * p_151238_4_);
    }

    /**
     * Maximum of the absolute value of two numbers.
     */
    public static double abs_max(double p_76132_0_, double p_76132_2_) {
        if (p_76132_0_ < 0.0D) {
            p_76132_0_ = -p_76132_0_;
        }

        if (p_76132_2_ < 0.0D) {
            p_76132_2_ = -p_76132_2_;
        }

        return p_76132_0_ > p_76132_2_ ? p_76132_0_ : p_76132_2_;
    }

    /**
     * Buckets an integer with specifed bucket sizes.  Args: i, bucketSize
     */
    public static int bucketInt(int p_76137_0_, int p_76137_1_) {
        return p_76137_0_ < 0 ? -((-p_76137_0_ - 1) / p_76137_1_) - 1 : p_76137_0_ / p_76137_1_;
    }

    public static int getRandomIntegerInRange(Random p_76136_0_, int p_76136_1_, int p_76136_2_) {
        return p_76136_1_ >= p_76136_2_ ? p_76136_1_ : p_76136_0_.nextInt(p_76136_2_ - p_76136_1_ + 1) + p_76136_1_;
    }

    public static float randomFloatClamp(Random p_151240_0_, float p_151240_1_, float p_151240_2_) {
        return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
    }

    public static double getRandomDoubleInRange(Random p_82716_0_, double p_82716_1_, double p_82716_3_) {
        return p_82716_1_ >= p_82716_3_ ? p_82716_1_ : p_82716_0_.nextDouble() * (p_82716_3_ - p_82716_1_) + p_82716_1_;
    }

    public static double average(long[] values) {
        long i = 0L;

        for (long j : values) {
            i += j;
        }

        return (double) i / (double) values.length;
    }

    public static boolean epsilonEquals(float p_180185_0_, float p_180185_1_) {
        return abs(p_180185_1_ - p_180185_0_) < 1.0E-5F;
    }

    public static int normalizeAngle(int p_180184_0_, int p_180184_1_) {
        return (p_180184_0_ % p_180184_1_ + p_180184_1_) % p_180184_1_;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static float wrapAngleTo180_float(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static double wrapAngleTo180_double(double value) {
        value = value % 360.0D;

        if (value >= 180.0D) {
            value -= 360.0D;
        }

        if (value < -180.0D) {
            value += 360.0D;
        }

        return value;
    }

    /**
     * parses the string as integer or returns the second parameter if it fails
     */
    public static int parseIntWithDefault(String p_82715_0_, int p_82715_1_) {
        try {
            return Integer.parseInt(p_82715_0_);
        } catch (Throwable var3) {
            return p_82715_1_;
        }
    }

    /**
     * parses the string as integer or returns the second parameter if it fails. this value is capped to par2
     */
    public static int parseIntWithDefaultAndMax(String p_82714_0_, int p_82714_1_, int p_82714_2_) {
        return Math.max(p_82714_2_, parseIntWithDefault(p_82714_0_, p_82714_1_));
    }

    /**
     * parses the string as double or returns the second parameter if it fails.
     */
    public static double parseDoubleWithDefault(String p_82712_0_, double p_82712_1_) {
        try {
            return Double.parseDouble(p_82712_0_);
        } catch (Throwable var4) {
            return p_82712_1_;
        }
    }

    public static double parseDoubleWithDefaultAndMax(String p_82713_0_, double p_82713_1_, double p_82713_3_) {
        return Math.max(p_82713_3_, parseDoubleWithDefault(p_82713_0_, p_82713_1_));
    }

    /**
     * Returns the input value rounded up to the next highest power of two.
     */
    public static int roundUpToPowerOfTwo(int value) {
        int i = value - 1;
        i = i | i >> 1;
        i = i | i >> 2;
        i = i | i >> 4;
        i = i | i >> 8;
        i = i | i >> 16;
        return i + 1;
    }

    /**
     * Is the given value a power of two?  (1, 2, 4, 8, 16, ...)
     */
    private static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    /**
     * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two of the given
     * value.  Optimized for cases where the input value is a power-of-two.  If the input value is not a power-of-two,
     * then subtract 1 from the return value.
     */
    private static int calculateLogBaseTwoDeBruijn(int value) {
        value = isPowerOfTwo(value) ? value : roundUpToPowerOfTwo(value);
        return multiplyDeBruijnBitPosition[(int) ((long) value * 125613361L >> 27) & 31];
    }

    /**
     * Efficiently calculates the floor of the base-2 log of an integer value.  This is effectively the index of the
     * highest bit that is set.  For example, if the number in binary is 0...100101, this will return 5.
     */
    public static int calculateLogBaseTwo(int value) {
        return calculateLogBaseTwoDeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
    }

    public static int func_154354_b(int p_154354_0_, int p_154354_1_) {
        if (p_154354_1_ == 0) {
            return 0;
        } else if (p_154354_0_ == 0) {
            return p_154354_1_;
        } else {
            if (p_154354_0_ < 0) {
                p_154354_1_ *= -1;
            }

            int i = p_154354_0_ % p_154354_1_;
            return i == 0 ? p_154354_0_ : p_154354_0_ + p_154354_1_ - i;
        }
    }

    public static int func_180183_b(float p_180183_0_, float p_180183_1_, float p_180183_2_) {
        return func_180181_b(floor_float(p_180183_0_ * 255.0F), floor_float(p_180183_1_ * 255.0F), floor_float(p_180183_2_ * 255.0F));
    }

    public static int func_180181_b(int p_180181_0_, int p_180181_1_, int p_180181_2_) {
        int i = (p_180181_0_ << 8) + p_180181_1_;
        i = (i << 8) + p_180181_2_;
        return i;
    }

    public static int func_180188_d(int p_180188_0_, int p_180188_1_) {
        int i = (p_180188_0_ & 16711680) >> 16;
        int j = (p_180188_1_ & 16711680) >> 16;
        int k = (p_180188_0_ & 65280) >> 8;
        int l = (p_180188_1_ & 65280) >> 8;
        int i1 = (p_180188_0_ & 255) >> 0;
        int j1 = (p_180188_1_ & 255) >> 0;
        int k1 = (int) ((float) i * (float) j / 255.0F);
        int l1 = (int) ((float) k * (float) l / 255.0F);
        int i2 = (int) ((float) i1 * (float) j1 / 255.0F);
        return p_180188_0_ & -16777216 | k1 << 16 | l1 << 8 | i2;
    }

    public static double func_181162_h(double p_181162_0_) {
        return p_181162_0_ - Math.floor(p_181162_0_);
    }

    public static long getCoordinateRandom(int x, int y, int z) {
        long i = (long) (x * 3129871) ^ (long) z * 116129781L ^ (long) y;
        i = i * i * 42317861L + i * 11L;
        return i;
    }

    public static UUID getRandomUuid(Random rand) {
        long i = rand.nextLong() & -61441L | 16384L;
        long j = rand.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
        return new UUID(i, j);
    }

    public static double func_181160_c(double p_181160_0_, double p_181160_2_, double p_181160_4_) {
        return (p_181160_0_ - p_181160_2_) / (p_181160_4_ - p_181160_2_);
    }

    public static double func_181159_b(double p_181159_0_, double p_181159_2_) {
        double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;

        if (Double.isNaN(d0)) {
            return Double.NaN;
        } else {
            boolean flag = p_181159_0_ < 0.0D;

            if (flag) {
                p_181159_0_ = -p_181159_0_;
            }

            boolean flag1 = p_181159_2_ < 0.0D;

            if (flag1) {
                p_181159_2_ = -p_181159_2_;
            }

            boolean flag2 = p_181159_0_ > p_181159_2_;

            if (flag2) {
                double d1 = p_181159_2_;
                p_181159_2_ = p_181159_0_;
                p_181159_0_ = d1;
            }

            double d9 = func_181161_i(d0);
            p_181159_2_ = p_181159_2_ * d9;
            p_181159_0_ = p_181159_0_ * d9;
            double d2 = field_181163_d + p_181159_0_;
            int i = (int) Double.doubleToRawLongBits(d2);
            double d3 = field_181164_e[i];
            double d4 = field_181165_f[i];
            double d5 = d2 - field_181163_d;
            double d6 = p_181159_0_ * d4 - p_181159_2_ * d5;
            double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
            double d8 = d3 + d7;

            if (flag2) {
                d8 = (Math.PI / 2D) - d8;
            }

            if (flag1) {
                d8 = Math.PI - d8;
            }

            if (flag) {
                d8 = -d8;
            }

            return d8;
        }
    }

    public static double func_181161_i(double p_181161_0_) {
        double d0 = 0.5D * p_181161_0_;
        long i = Double.doubleToRawLongBits(p_181161_0_);
        i = 6910469410427058090L - (i >> 1);
        p_181161_0_ = Double.longBitsToDouble(i);
        p_181161_0_ = p_181161_0_ * (1.5D - d0 * p_181161_0_ * p_181161_0_);
        return p_181161_0_;
    }

    public static int func_181758_c(float p_181758_0_, float p_181758_1_, float p_181758_2_) {
        int i = (int) (p_181758_0_ * 6.0F) % 6;
        float f = p_181758_0_ * 6.0F - (float) i;
        float f1 = p_181758_2_ * (1.0F - p_181758_1_);
        float f2 = p_181758_2_ * (1.0F - f * p_181758_1_);
        float f3 = p_181758_2_ * (1.0F - (1.0F - f) * p_181758_1_);
        float f4;
        float f5;
        float f6;

        switch (i) {
            case 0:
                f4 = p_181758_2_;
                f5 = f3;
                f6 = f1;
                break;

            case 1:
                f4 = f2;
                f5 = p_181758_2_;
                f6 = f1;
                break;

            case 2:
                f4 = f1;
                f5 = p_181758_2_;
                f6 = f3;
                break;

            case 3:
                f4 = f1;
                f5 = f2;
                f6 = p_181758_2_;
                break;

            case 4:
                f4 = f3;
                f5 = f1;
                f6 = p_181758_2_;
                break;

            case 5:
                f4 = p_181758_2_;
                f5 = f1;
                f6 = f2;
                break;

            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + p_181758_0_ + ", " + p_181758_1_ + ", " + p_181758_2_);
        }

        int j = clamp_int((int) (f4 * 255.0F), 0, 255);
        int k = clamp_int((int) (f5 * 255.0F), 0, 255);
        int l = clamp_int((int) (f6 * 255.0F), 0, 255);
        return j << 16 | k << 8 | l;
    }
}
