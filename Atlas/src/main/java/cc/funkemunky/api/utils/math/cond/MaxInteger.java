package cc.funkemunky.api.utils.math.cond;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MaxInteger {
    @Setter
    private int value;
    private final int max;

    public int add(int amount) {
        return value = Math.min(max, value + amount);
    }

    public int add() {
        return add(1);
    }

    public int subtract(int amount) {
        return value = Math.max(0, value - amount);
    }

    public int subtract() {
        return subtract(1);
    }

    public int value() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
