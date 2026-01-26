package cc.funkemunky.api.utils.math.cond;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MaxDouble {
    @Setter
    private double value;
    private final double max;
    
    public double add(double amount) {
        return value = Math.min(max, value + amount);
    }
    
    public double add() {
        return add(1);
    }
    
    public double subtract(double amount) {
        return value = Math.max(0, value - amount);
    }
    
    public double subtract() {
        return subtract(1);
    }
    
    public double value() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
