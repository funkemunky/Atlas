package cc.funkemunky.api.utils.math;

import lombok.Getter;

@Getter
public class SimpleAverage {
    private double total;
    private int count;
    private final int size;
    private final double initial;

    public SimpleAverage(int size, double initial) {
        this.size = size;

        this.initial = initial;
    }

    public void add(double value) {
        total+= value;

        if(++count > size) {
            total-= getAverage();
            count--;
        }
    }

    public double getAverage() {
        if(count == 0) return initial;
        return total / count;
    }

    public void clear() {
        count = 0;
        total = 0;
    }
}
