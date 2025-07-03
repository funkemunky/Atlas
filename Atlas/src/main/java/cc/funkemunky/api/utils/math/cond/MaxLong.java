package cc.funkemunky.api.utils.math.cond;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MaxLong {
    @Setter
    private long value;
    private final long max;

    public long add(long amount) {
        return value = Math.min(max, value + amount);
    }

    public long add() {
        return add(1);
    }

    public long subtract(long amount) {
        return value = Math.max(0, value - amount);
    }

    public long subtract() {
        return subtract(1);
    }

    public long value() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
