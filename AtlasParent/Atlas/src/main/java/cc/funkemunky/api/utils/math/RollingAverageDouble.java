package cc.funkemunky.api.utils.math;

import lombok.Getter;

import java.util.Arrays;

public class RollingAverageDouble {

    private final int size;
    private double[] array;

    private int index;

    @Getter
    private double average;

    public RollingAverageDouble(int size, double initial) {
        this.size = size;
        array = new double[size];
        average = initial;
        initial /= size;
        Arrays.fill(array, initial);
    }

    public void add(double value) {
        value /= size;
        average -= array[index];
        average += value;
        array[index] = value;
        index = (index + 1) % size;

        if(Double.isNaN(average)) {
            average = 0;
            array = new double[size];
            index = 0;
        }
        //System.out.println(Arrays.stream(array).mapToObj(d -> String.format("%.2f", d)).collect(Collectors.joining(",")));
    }

    public void clearValues() {
        average = 0;
        Arrays.fill(array, 0);
    }
}
