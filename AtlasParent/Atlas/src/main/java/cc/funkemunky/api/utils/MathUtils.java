package cc.funkemunky.api.utils;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class MathUtils {

    public static double offset(Vector from, Vector to) {
        from.setY(0);
        to.setY(0);

        return to.subtract(from).length();
    }

    public static boolean playerMoved(Location from, Location to) {
        return playerMoved(from.toVector(), to.toVector());
    }

    public static byte getByte(int num) {
        if(num > Byte.MAX_VALUE || num < Byte.MIN_VALUE) {
            throw new NumberFormatException("Integer " + num + " too large to cast to data format byte!"
                    + " (max=" + Byte.MAX_VALUE + " min=" + Byte.MIN_VALUE + ")");
        }

        return (byte) num;
    }

    public static short getShort(int num) {
        if(num > Short.MAX_VALUE || num < Short.MIN_VALUE) {
            throw new NumberFormatException("Integer " + num + " too large to cast to data format short!"
                    + " (max=" + Short.MAX_VALUE + " min=" + Short.MIN_VALUE + ")");
        }
        return (short) num;
    }

    /* Stolen from Bukkit */
    public static Vector getDirection(KLocation loc) {
        Vector vector = new Vector();
        double rotX = loc.yaw;
        double rotY = loc.pitch;
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }

    public static boolean approxEquals(double accuracy, double equalTo, double... equals) {
        return Arrays.stream(equals).allMatch(equal -> MathUtils.getDelta(equalTo, equal) < accuracy);
    }

    public static boolean approxEquals(double accuracy, int equalTo, int... equals) {
        return Arrays.stream(equals).allMatch(equal -> MathUtils.getDelta(equalTo, equal) < accuracy);
    }

    public static boolean approxEquals(double accuracy, long equalTo, long... equals) {
        return Arrays.stream(equals).allMatch(equal -> MathUtils.getDelta(equalTo, equal) < accuracy);
    }

    public static double getDistanceToBox(Vector vec, BoundingBox box) {
        return vec.distance(getCenterOfBox(box));
    }

    public static Vector getCenterOfBox(BoundingBox box) {
        return box.getMinimum().midpoint(box.getMaximum());
    }

    //Returns -1 if fails.
    public static <T extends Number> T tryParse(String string) {
        try {
            return (T)(Number)Double.parseDouble(string);
        } catch(NumberFormatException e) {

        }
        return (T)(Number)(-1);
    }

    //A lighter version of the Java hypotenuse function.
    public static double hypot(double... value) {
        double total = 0;

        for (double val : value) {
            total += (val * val);
        }

        return Math.sqrt(total);
    }

    public static float hypot(float... value) {
        float total = 0;

        for (float val : value) {
            total += (val * val);
        }

        return (float) Math.sqrt(total);
    }

    public static double get3DDistance(Vector one, Vector two) {
        return hypot(one.getX() - two.getX(), one.getY() - two.getY(), one.getZ() - two.getZ());
    }

    public static boolean playerMoved(Vector from, Vector to) {
        return from.distance(to) > 0;
    }

    public static boolean playerLooked(Location from, Location to) {
        return (from.getYaw() - to.getYaw() != 0) || (from.getPitch() - to.getPitch() != 0);
    }
    public static boolean elapsed(long time, long needed) {
        return Math.abs(System.currentTimeMillis() - time) >= needed;
    }

    //Euclid's algorithim
    public static long gcd(long a, long b)
    {
        while (b > 0)
        {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    //Euclid's algorithim
    public static long gcd(long... input)
    {
        long result = input[0];
        for(int i = 1; i < input.length; i++) result = gcd(result, input[i]);
        return result;
    }

    // Returns the absolute value of n-mid*mid*mid
    static double diff(double n,double mid)
    {
        if (n > (mid*mid*mid))
            return (n-(mid*mid*mid));
        else
            return ((mid*mid*mid) - n);
    }

    // Returns cube root of a no n
    public static double cbrt(double n)
    {
        // Set start and end for binary search
        double start = 0, end = n;

        // Set precision
        double e = 0.0000001;

        double mid = -1;
        double error = 1000;

        long ticks = 0;
        while (error > e)
        {
            mid = (start + end)/2;
            error = diff(n, mid);

            // If error is less than e then mid is
            // our answer so return mid

            // If mid*mid*mid is greater than n set
            // end = mid
            if ((mid*mid*mid) > n)
                end = mid;

                // If mid*mid*mid is less than n set
                // start = mid
            else
                start = mid;

            if(error > e && ticks++ > 3E4) {
                return -1;
            }
        }
        return mid;
    }

    //A much lighter but very slightly less accurate Math.sqrt.
    @Deprecated
    public static double sqrt(double number) {
        if(number == 0) return 0;
        double t;
        double squareRoot = number / 2;

        do {
            t = squareRoot;
            squareRoot = (t + (number / t)) / 2;
        } while ((t - squareRoot) != 0);

        return squareRoot;
    }

    public static Vector getDirection(double yaw, double pitch) {
        Vector vector = new Vector();
        vector.setY(-Math.sin(Math.toRadians(pitch)));
        double xz = Math.cos(Math.toRadians(pitch));
        vector.setX(-xz * Math.sin(Math.toRadians(yaw)));
        vector.setZ(xz * Math.cos(Math.toRadians(yaw)));
        return vector;
    }

    public static float sqrt(float number) {
        if(number == 0) return 0;
        float t;

        float squareRoot = number / 2;

        do {
            t = squareRoot;
            squareRoot = (t + (number / t)) / 2;
        } while ((t - squareRoot) != 0);

        return squareRoot;
    }

    public static float normalizeAngle(float yaw) {
        return yaw % 360;
    }

    public static double normalizeAngle(double yaw) {
        return yaw % 360;
    }

    public static float getAngleDelta(float one, float two) {
        float delta = getDelta(one, two) % 360f;

        if(delta > 180) delta = 360 - delta;
        return delta;
    }

    //Euclid's algorithim
    public static long lcm(long a, long b)
    {
        return a * (b / gcd(a, b));
    }

    //Euclid's algorithim
    public static long lcm(long... input)
    {
        long result = input[0];
        for(int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return result;
    }

    public static float getDelta(float one, float two) {
        return Math.abs(one - two);
    }

    public static double getDelta(double one, double two) {
        return Math.abs(one - two);
    }

    public static long getDelta(long one, long two) {
        return Math.abs(one - two);
    }

    public static long getDelta(int one, int two) {
        return Math.abs(one - two);
    }

    public static long elapsed(long time) {
        return Math.abs(System.currentTimeMillis() - time);
    }

    public static double getHorizontalDistance(Location from, Location to) {
        double deltaX = to.getX() - from.getX(), deltaZ = to.getZ() - from.getZ();
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    public static double stdev(Collection<Double> list) {
        double sum = 0.0;
        double mean;
        double num = 0.0;
        double numi;
        double deno = 0.0;

        for (double i : list) {
            sum += i;
        }
        mean = sum / list.size();

        for (double i : list) {
            numi = Math.pow(i - mean, 2);
            num += numi;
        }

        return Math.sqrt(num / list.size());
    }

    public static int millisToTicks(long millis) {
        return (int) Math.ceil(millis / 50D);
    }

    public static double getVerticalDistance(Location from, Location to) {
        return Math.abs(from.getY() - to.getY());
    }

    public static int getDistanceToGround(Player p) {
        Location loc = p.getLocation().clone();
        double y = loc.getBlockY();
        int distance = 0;
        for (double i = y; i >= 0.0; i -= 1.0) {
            loc.setY(i);
            if (BlockUtils.getBlock(loc).getType().isSolid() || BlockUtils.getBlock(loc).isLiquid()) break;
            ++distance;
        }
        return distance;
    }

    public static double trim(int degree, double d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = String.valueOf(format) + "#";
        }
        DecimalFormat twoDForm = new DecimalFormat(format);
        return Double.parseDouble(twoDForm.format(d).replaceAll(",", "."));
    }

    public static float trimFloat(int degree, float d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = String.valueOf(format) + "#";
        }
        DecimalFormat twoDForm = new DecimalFormat(format);
        return Float.parseFloat(twoDForm.format(d).replaceAll(",", "."));
    }

    public static double getYawDifference(Location one, Location two) {
        return Math.abs(one.getYaw() - two.getYaw());
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        if(Double.isNaN(value) || Double.isInfinite(value)) return value;

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //May not be the best on performance. Let me know if you have a better way to calculate mode.
    public static <T extends Number> T getMode(Collection<T> collect) {
        Map<T, Integer> repeated = new HashMap<>();

        //Sorting each value by how to repeat into a map.
        collect.forEach(val -> {
            int number = repeated.getOrDefault(val, 0);

            repeated.put(val, number + 1);
        });

        //Calculating the largest value to the key, which would be the mode.
        return (T) repeated.keySet().stream()
                .map(key -> new Tuple<>(key, repeated.get(key))) //We map it into a Tuple for easier sorting.
                .max(Comparator.comparing(tup -> tup.two, Comparator.naturalOrder()))
                .orElseThrow(NullPointerException::new).one;
    }

    public static double round(double value, int places, RoundingMode mode) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, mode);
        return bd.doubleValue();
    }

    public static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.UP);
        return bd.doubleValue();
    }

    public static float round(float value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static float round(float value, int places, RoundingMode mode) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, mode);
        return bd.floatValue();
    }

    public static float round(float value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.UP);
        return bd.floatValue();
    }

    public static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < var2 ? var2 - 1 : var2;
    }

    public static float yawTo180F(float flub) {
        if ((flub %= 360.0f) >= 180.0f) {
            flub -= 360.0f;
        }
        if (flub < -180.0f) {
            flub += 360.0f;
        }
        return flub;
    }

    public static double yawTo180D(double dub) {
        if ((dub %= 360.0) >= 180.0) {
            dub -= 360.0;
        }
        if (dub < -180.0) {
            dub += 360.0;
        }
        return dub;
    }

    public static double getDirection(Location from, Location to) {
        if (from == null || to == null) {
            return 0.0;
        }
        double difX = to.getX() - from.getX();
        double difZ = to.getZ() - from.getZ();
        return MathUtils.yawTo180F((float) (FastTrig.fast_atan2(difZ, difX) * 180.0 / 3.141592653589793) - 90.0f);
    }

    public static float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (FastTrig.fast_atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-FastTrig.fast_atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static float[] getRotations(LivingEntity origin, LivingEntity point) {
        Location two = point.getLocation(), one = origin.getLocation();
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (FastTrig.fast_atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-FastTrig.fast_atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static boolean isLookingTowardsEntity(Location from, Location to, LivingEntity entity) {
        float[] rotFrom = getRotations(from, entity.getLocation()), rotTo = getRotations(to, entity.getLocation());
        float deltaOne = getDelta(from.getYaw(), rotTo[0]), deltaTwo = getDelta(to.getYaw(), rotTo[1]);
        float offsetFrom = getDelta(yawTo180F(from.getYaw()), yawTo180F(rotFrom[0])), offsetTo = getDelta(yawTo180F(to.getYaw()), yawTo180F(rotTo[0]));

        return (deltaOne > deltaTwo && offsetTo > 15) || (MathUtils.getDelta(offsetFrom, offsetTo) < 1 && offsetTo < 10);
    }

    public static double[] getOffsetFromEntity(Player player, LivingEntity entity) {
        double yawOffset = Math.abs(MathUtils.yawTo180F(player.getEyeLocation().getYaw()) - MathUtils.yawTo180F(MathUtils.getRotations(player.getLocation(), entity.getLocation())[0]));
        double pitchOffset = Math.abs(Math.abs(player.getEyeLocation().getPitch()) - Math.abs(MathUtils.getRotations(player.getLocation(), entity.getLocation())[1]));
        return new double[]{yawOffset, pitchOffset};
    }

    public static double[] getOffsetFromLocation(Location one, Location two) {
        double yaw = MathUtils.getRotations(one, two)[0];
        double pitch = MathUtils.getRotations(one, two)[1];
        double yawOffset = Math.abs(yaw - MathUtils.yawTo180F(one.getYaw()));
        double pitchOffset = Math.abs(pitch - one.getPitch());
        return new double[]{yawOffset, pitchOffset};
    }
}

