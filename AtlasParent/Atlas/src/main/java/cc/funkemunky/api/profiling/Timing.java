package cc.funkemunky.api.profiling;

import cc.funkemunky.api.utils.math.RollingAverage;
import cc.funkemunky.api.utils.math.RollingAverageDouble;
import cc.funkemunky.api.utils.math.RollingAverageLong;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Timing {
    public final String name;
    public int calls;
    public long call, total, lastCall;
    public double stdDev;
    public RollingAverageDouble average = new RollingAverageDouble(40, 0);
}
