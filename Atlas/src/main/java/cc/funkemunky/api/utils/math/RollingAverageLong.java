package cc.funkemunky.api.utils.math;

import lombok.Getter;

import java.util.Arrays;

public class RollingAverageLong {

    private final int size;
    private final double[] array;

    private int index;

    @Getter
    private long average;

    public RollingAverageLong(int size, long initial) {
        this.size = size;
        array = new double[size];
        average = initial;
        initial /= size;
        Arrays.fill(array, initial);
    }

    public void add(long value) {
        value /= size;
        average -= array[index];
        average += value;
        array[index] = value;
        index = (index + 1) % size;
    }
}
