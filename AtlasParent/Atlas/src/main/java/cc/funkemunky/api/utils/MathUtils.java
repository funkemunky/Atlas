package cc.funkemunky.api.utils;

import cc.funkemunky.api.tinyprotocol.packet.types.MathHelper;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MathUtils {

    public static double offset(Vector from, Vector to) {
        from.setY(0);
        to.setY(0);

        return to.subtract(from).length();
    }

    public static boolean playerMoved(Location from, Location to) {
        return playerMoved(from.toVector(), to.toVector());
    }

    public static double getDistanceWithoutRoot(KLocation one, KLocation two) {
        double deltaX = one.x - two.x, deltaY = one.y - two.y, deltaZ = one.z - two.z;

        return (deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ);
    }

    public static boolean isSameLocation(KLocation one, KLocation two) {
        return one.x == two.x && one.y == two.y && one.z == two.z;
    }


    public static double max(double... values) {
        return Arrays.stream(values).max().orElse(Double.MAX_VALUE);
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public static int length(double value) {
        return String.valueOf(value).length();
    }

    public static double getGrid(final Collection<Float> entry) {
        double average = 0.0;
        double min = 0.0, max = 0.0;

        for (final double number : entry) {
            if (number < min) min = number;
            if (number > max) max = number;
            average += number;
        }

        average /= entry.size();

        return (max - average) - min;
    }

    //Skidded from Luke.
    public static double getAngle(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return -1;
        Vector playerRotation = new Vector(loc1.getYaw(), loc1.getPitch(), 0.0f);
        loc1.setY(0);
        loc2.setY(0);
        val rot = MathUtils.getRotations(loc1, loc2);
        Vector expectedRotation = new Vector(rot[0], rot[1], 0);
        return MathUtils.yawTo180D(playerRotation.getX() - expectedRotation.getX());
    }

    public static double getAngle(KLocation loc1, KLocation loc2) {
        return getAngle(loc1.toLocation(null), loc2.toLocation(null));
    }

    public static float distanceBetweenAngles(float a, float b) {
        final float first = a % 360;
        final float second = b % 360;

        final float delta = Math.abs(first - second);

        return (float) Math.abs(Math.min(360.0 - delta, delta));
    }

    public static float getDistanceBetweenAngles(final float angle1, final float angle2) {
        float distance = Math.abs(angle1 - angle2) % 360.0f;
        if (distance > 180.0f) {
            distance = 360.0f - distance;
        }
        return distance;
    }

    //Args: Tuple (a) is low outliers, Tupe (B) is high outliers
    public static Tuple<List<Double>, List<Double>> getOutliers(List<Double> values) {

        if(values.size() < 4) return new Tuple<>(new ArrayList<>(), new ArrayList<>());

        double q1 = getMedian(values.subList(0, values.size() / 2)),
                q3 = getMedian(values.subList(values.size() / 2, values.size()));
        double iqr = Math.abs(q1 - q3);

        double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        val tuple = new Tuple<List<Double>, List<Double>>(new ArrayList<>(), new ArrayList<>());

        for (Double value : values) {
            if(value < lowThreshold) tuple.one.add(value);
            if(value < lowThreshold) tuple.one.add(value);
            else if(value > highThreshold) tuple.two.add(value);
        }

        return tuple;
    }

    public static Tuple<List<Float>, List<Float>> getOutliersFloat(List<Float> values) {
        if(values.size() < 4) return new Tuple<>(new ArrayList<>(), new ArrayList<>());

        double q1 = getMedian(values.subList(0, values.size() / 2)),
                q3 = getMedian(values.subList(values.size() / 2, values.size()));
        double iqr = Math.abs(q1 - q3);

        double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        val tuple = new Tuple<List<Float>, List<Float>>(new ArrayList<>(), new ArrayList<>());

        for (Float value : values) {
            if(value < lowThreshold) tuple.one.add(value);
            else if(value > highThreshold) tuple.two.add(value);
        }

        return tuple;
    }

    public static Tuple<List<Long>, List<Long>> getOutliersLong(List<Long> collection) {
        List<Long> values = new ArrayList<>(collection);

        if(values.size() < 4) return new Tuple<>(new ArrayList<>(), new ArrayList<>());

        double q1 = getMedian(values.subList(0, values.size() / 2)),
                q3 = getMedian(values.subList(values.size() / 2, values.size()));
        double iqr = Math.abs(q1 - q3);

        double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        val tuple = new Tuple<List<Long>, List<Long>>(new ArrayList<>(), new ArrayList<>());

        for (Long value : values) {
            if(value < lowThreshold) tuple.one.add(value);
            else if(value > highThreshold) tuple.two.add(value);
        }

        return tuple;
    }

    public static double getMedian(List<Double> data) {
        if(data.size() > 1) {
            if (data.size() % 2 == 0)
                return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
            else
                return data.get(Math.round(data.size() / 2f));
        }
        return 0;
    }
    public static double getMedian(Iterable<? extends Number> iterable) {
        List<Double> data = new ArrayList<>();

        for (Number number : iterable) {
            data.add(number.doubleValue());
        }

        return getMedian(data);
    }

    //Copied from apache math Kurtosis class.
    public static double getKurtosisApache(Iterable<? extends Number> iterable) {
        List<Double> values = new ArrayList<>();

        double total = 0;
        double kurt = Double.NaN;
        for (Number number : iterable) {
            double v = number.doubleValue();
            total+= v;
            values.add(v);
        }

        if(values.size() < 2) return kurt;

        double mean = total / values.size();
        double stdDev = MathUtils.stdev(values);
        double accum3 = 0.0D;

        for (Double value : values) {
            accum3 += Math.pow(value - mean, 4.0D);
        }

        accum3 /= Math.pow(stdDev, 4.0D);
        double n0 = values.size();
        double coefficientOne = n0 * (n0 + 1.0D) / ((n0 - 1.0D) * (n0 - 2.0D) * (n0 - 3.0D));
        double termTwo = 3.0D * Math.pow(n0 - 1.0D, 2.0D) / ((n0 - 2.0D) * (n0 - 3.0D));
        kurt = coefficientOne * accum3 - termTwo;

        return kurt;
    }

    public static double getKurtosis(final Iterable<? extends Number> iterable) {
        double n = 0.0;
        double n2 = 0.0;

        for (Number number : iterable) {
            n += number.doubleValue();
            ++n2;
        }

        if (n2 < 3.0) {
            return 0.0;
        }
        final double n3 = n2 * (n2 + 1.0) / ((n2 - 1.0) * (n2 - 2.0) * (n2 - 3.0));
        final double n4 = 3.0 * Math.pow(n2 - 1.0, 2.0) / ((n2 - 2.0) * (n2 - 3.0));
        final double n5 = n / n2;
        double n6 = 0.0;
        double n7 = 0.0;
        for (final Number n8 : iterable) {
            n6 += Math.pow(n5 - n8.doubleValue(), 2.0);
            n7 += Math.pow(n5 - n8.doubleValue(), 4.0);
        }
        return n3 * (n7 / Math.pow(n6 / n2, 2.0)) - n4;
    }

    public static float pow(float number, int times) {
        float answer = number;

        if(times <= 0) return 0;

        for(int i = 1 ; i < times ; i++) {
            answer*= number;
        }

        return answer;
    }

    public static double varianceSquared(final Number n, final Iterable<? extends Number> iterable) {
        double n2 = 0.0;
        int n3 = 0;

        for (Number number : iterable) {
            n2 += Math.pow((number).doubleValue() - n.doubleValue(), 2.0);
            ++n3;
        }

        return (n2 == 0.0) ? 0.0 : (n2 / (n3 - 1));
    }

    public static List<Double> getModes(final Iterable<? extends Number> iterable) {
        List<Double> numbers = new ArrayList<>();

        for (Number number : iterable) {
            numbers.add(number.doubleValue());
        }
        final Map<Double, Long> countFrequencies = numbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        final double maxFrequency = countFrequencies.values().stream()
                .mapToDouble(count -> count)
                .max().orElse(-1);

        return countFrequencies.entrySet().stream()
                .filter(tuple -> tuple.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    //Copied from apache math Skewness class.
    public static double getSkewnessApache(Iterable<? extends Number> iterable) {
        List<Double> values = new ArrayList<>();

        double total = 0;
        double skew = Double.NaN;
        for (Number number : iterable) {
            double v = number.doubleValue();
            total+= v;
            values.add(v);
        }

        if(values.size() < 2) return skew;

        double m = total / values.size();
        double accum = 0.0D;
        double accum2 = 0.0D;

        for (Double value : values) {
            double d = value - m;
            accum += d * d;
            accum2 += d;
        }

        double variance = (accum - accum2 * accum2 / values.size()) / (values.size() - 1);
        double accum3 = 0.0D;

        for (Double value : values) {
            double d = value - m;
            accum3 += d * d * d;
        }

        accum3 /= variance * Math.sqrt(variance);
        double n0 = values.size();
        skew = n0 / ((n0 - 1.0D) * (n0 - 2.0D)) * accum3;

        return skew;
    }

    public static double getSkewness(final Iterable<? extends Number> iterable) {
        double sum = 0;
        int buffer = 0;

        final List<Double> numberList = new ArrayList<>();

        for (Number num : iterable) {
            sum += num.doubleValue();
            buffer++;

            numberList.add(num.doubleValue());
        }

        Collections.sort(numberList);

        final double mean =  sum / buffer;
        final double median = (buffer % 2 != 0) ? numberList.get(buffer / 2) : (numberList.get((buffer - 1) / 2) + numberList.get(buffer / 2)) / 2;

        return 3 * (mean - median) / deviationSquared(iterable);
    }

    public static double stdev(final Iterable<? extends Number> iterable) {
        double sum = 0.0f;
        double num = 0.0f;

        final List<Double> list = new ArrayList<>();

        for (Number number : iterable) {
            list.add(number.doubleValue());
        }

        for (Double v : list) {
            sum+= v;
        }

        double mean = sum / (float)list.size();

        for (Double v : list) {
            num+= Math.pow(v - mean, 2.0D);
        }

        return MathHelper.sqrt(num / (double)list.size());
    }

    public static double deviationSquared(final Iterable<? extends Number> iterable) {
        double n = 0.0;
        int n2 = 0;

        for (Number anIterable : iterable) {
            n += (anIterable).doubleValue();
            ++n2;
        }
        final double n3 = n / n2;
        double n4 = 0.0;

        for (Number anIterable : iterable) {
            n4 += Math.pow(anIterable.doubleValue() - n3, 2.0);
        }

        return (n4 == 0.0) ? 0.0 : (n4 / (n2 - 1));
    }

    public static int getDecimalCount(float number) {
        return String.valueOf(number).split("\\.")[1].length();
    }

    public static int getDecimalCount(double number) {
        return String.valueOf(number).split("\\.")[1].length();
    }

    public static float clampToVanilla(float s, float angle) {
        float f = (s * 0.6f + .2f);
        float f2 = f * f * f * 1.2f;
        return angle - (angle % f2);
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

