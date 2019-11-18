package cc.funkemunky.api.utils.objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Stream;

public class Interval<T> extends LinkedList<T> {

    private long x, max;

    public Interval(long x, long max) {
        this.x = x;
        this.max = max;
    }

    public Interval(Interval in) {
        this.x = in.x;
        this.max = in.max;
    }

    public double average() {
        if(size() > 0) {
            if(getFirst() instanceof Long) {
                return streamNumber().mapToLong(val -> (long) val).average().orElse(0.0);
            } else if(getFirst() instanceof Integer) {
                return streamNumber().mapToInt(val -> (int) val).average().orElse(0.0);
            } else if(getFirst() instanceof Float) {
               return streamNumber().mapToDouble(val -> (float)val).average().orElse(0.0);
            } else return streamNumber().mapToDouble(val -> (double)val).average().orElse(0.0);
        }
        return 0;
    }

    public double frequency(double freq) {
        return Collections.frequency(this, freq);
    }

    public long distinctCount() {
        return streamNumber().distinct().count();
    }

    public Stream<Number> distinct() {
        return streamNumber().distinct();
    }

    public double std() {
        double average = average();
        if(size() > 0) {
            if(getFirst() instanceof Long) {
                return Math.sqrt(streamNumber()
                        .mapToLong(val -> (long)Math.pow((long)val - average, 2)).average().orElse(0.0));
            } else if(getFirst() instanceof Integer) {
                return Math.sqrt(streamNumber()
                        .mapToInt(val -> (int) Math.pow((int)val - average, 2)).average().orElse(0.0));
            } else if(getFirst() instanceof Float) {
                return Math.sqrt(streamNumber()
                        .mapToDouble(val -> Math.pow((float)val - average, 2)).average().orElse(0));
            } else return Math.sqrt(streamNumber()
                    .mapToDouble(val -> Math.pow((double)val - average, 2)).average().orElse(0));
        }
        return 0;
    }

    public double max() {
        if(size() > 0) {
            if(getFirst() instanceof Long) {
                return streamNumber().mapToLong(val -> (long) val).max().orElse(0);
            } else if(getFirst() instanceof Integer) {
                return streamNumber().mapToInt(val -> (int) val).max().orElse(0);
            } else {
                return streamNumber().mapToDouble(val -> (double)val).max().orElse(0);
            }
        }
        return 0;
    }

    public double min() {
        if(size() > 0) {
            if(getFirst() instanceof Long) {
                return streamNumber().mapToLong(val -> (long) val).min().orElse(0);
            } else if(getFirst() instanceof Integer) {
                return streamNumber().mapToInt(val -> (int) val).min().orElse(0);
            } else {
                return streamNumber().mapToDouble(val -> (double)val).min().orElse(0);
            }
        }
        return 0;
    }

    public boolean add(T x) {
        if (size() > max) {
            remove(size() - 1);
        }
        return super.add(x);
    }

    public void clearIfMax() {
        if (size() == max) {
            this.clear();
        }
    }

    public Stream<Number> streamNumber() {
        return (Stream<Number>)super.stream();
    }
}