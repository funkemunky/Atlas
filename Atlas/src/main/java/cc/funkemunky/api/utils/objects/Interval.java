package cc.funkemunky.api.utils.objects;

import cc.funkemunky.api.utils.MathUtils;

import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class Interval extends LinkedList<Double> {

    private long max;

    public Interval(long max) {
        this.max = max;
    }

    public Interval(Interval in) {
        this.max = in.max;
    }

    public double average() {
        return getDoubleStream().summaryStatistics().getAverage();
    }

    public DoubleSummaryStatistics getSummary() {
        return getDoubleStream().summaryStatistics();
    }
    public double frequency(double freq) {
        return Collections.frequency(this, freq);
    }

    public long distinctCount() {
        return getDoubleStream().distinct().count();
    }

    public Stream<Double> distinct() {
        return stream().distinct();
    }

    public double std() {
        return MathUtils.stdev(this);
    }

    public double max() {
        return getDoubleStream().summaryStatistics().getMax();
    }

    public double min() {
        return getDoubleStream().summaryStatistics().getMin();
    }

    public boolean add(double x) {
        if (size() == max) {
            removeLast();
        }
        addFirst(x);
        return true;
    }

    public void clearIfMax() {
        if (size() == max) {
            this.clear();
        }
    }
    
    public DoubleStream getDoubleStream() { 
        return stream().mapToDouble(val -> val);
    }
}
