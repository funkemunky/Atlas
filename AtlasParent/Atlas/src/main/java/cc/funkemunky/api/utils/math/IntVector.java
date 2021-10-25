package cc.funkemunky.api.utils.math;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.util.Vector;

@AllArgsConstructor
@NoArgsConstructor
public class IntVector {
    @Getter
    @Setter
    private int x, y, z;

    public IntVector clone() {
        return new IntVector(x, y, z);
    }

    public Vector toBukkitVector() {
        return new Vector(x, y, z);
    }
}
