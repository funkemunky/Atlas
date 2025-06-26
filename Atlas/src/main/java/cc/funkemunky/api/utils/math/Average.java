package cc.funkemunky.api.utils.math;

import java.util.HashSet;
import java.util.Set;

public class Average {

    private final Set<Double> values;
    private final int limit;


    public Average(int limit) {
        this.limit = limit;
        values = new HashSet<>();
    }

    public void addValue(double value) {
        if (value != 0) values.add(value);
    }

    public void clearValues() {
        values.clear();
    }

    public boolean isAtLimit() {
        return values.size() >= limit;
    }

    public double getAverage(boolean clear) {
        double output = 0;
        int amount = 0;
        for (double value : values) {
            output = output + value;
            amount++;
        }

        if (isAtLimit()) clearValues();

        return (output / amount);
    }
}
