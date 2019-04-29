package cc.funkemunky.api.utils.math;

public class RollingAverage {

    private final int size;
    private double[] samples;
    private long[] times;
    private long time;
    private double total;
    private int index = 0;

    public RollingAverage(int size) {
        this.size = size;
        this.time = (long) size * 1000000000L;
        this.total = (double) (20000000000L * (long) size);
        this.samples = new double[size];
        this.times = new long[size];

        for (int i = 0; i < size; ++i) {
            this.samples[i] = 20.0D;
            this.times[i] = 1000000000L;
        }

    }

    public void add(double x, long t) {
        this.time -= this.times[this.index];
        this.total -= this.samples[this.index] * (double) this.times[this.index];
        this.samples[this.index] = x;
        this.times[this.index] = t;
        this.time += t;
        this.total += x * (double) t;
        if (++this.index == this.size) {
            this.index = 0;
        }
    }

    public void clearValues() {
        this.time = (long) size * 1000000000L;
        this.total = (double) (20000000000L * (long) size);
        this.samples = new double[size];
        this.times = new long[size];

        for (int i = 0; i < size; ++i) {
            this.samples[i] = 20.0D;
            this.times[i] = 1000000000L;
        }
    }

    public double getAverage() {
        return this.total / (double) this.time;
    }
}
