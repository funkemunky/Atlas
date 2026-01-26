package cc.funkemunky.api.utils.math.cond;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MaxFloat {
    @Setter
    private float value;
    private final float max;

    public float add(float amount) {
        return value = Math.min(max, value + amount);
    }

    public float add() {
        return add(1);
    }

    public float subtract(float amount) {
        return value = Math.max(0, value - amount);
    }

    public float subtract() {
        return subtract(1);
    }

    public float value() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
