package cc.funkemunky.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockBounds {
    public List<BoundingBox> bounds = new ArrayList<>();
    public int id;

    public BlockBounds(int id, BoundingBox... boxes) {
        bounds.addAll(Arrays.asList(boxes));
    }
}
