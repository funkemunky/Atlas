package cc.funkemunky.api.profiling;

import cc.funkemunky.api.utils.MathHelper;
import cc.funkemunky.api.utils.Tuple;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class BaseProfiler implements Profiler {
    @Getter
    private final Map<String, Timing> timingsMap = new HashMap<>();
    public long lastSample = 0, lastReset;
    public int totalCalls = 0;
    public long start = 0;

    public BaseProfiler() {
    }

    @Override
    public void start() {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        start(stack.getMethodName());

        if(start == 0) start = System.currentTimeMillis();
    }

    @Override
    public void start(String name) {
        Timing timing = getTiming(name);
        timing.lastCall = System.nanoTime();

        if(start == 0) start = System.currentTimeMillis();
    }

    @Override
    public void stop() {
        if(System.currentTimeMillis() - lastReset < 100L) return;
        long extense = System.nanoTime();
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        stop(stack.getMethodName(), extense);
        totalCalls++;
    }

    @Override
    public void reset() {
        lastSample = totalCalls = 0;
        timingsMap.clear();
        start = lastReset = System.currentTimeMillis();
    }

    //Returns Tuple<Total Calls, Result>
    @Override
    public Map<String, Tuple<Integer, Double>> results(ResultsType type) {
        Map<String, Tuple<Integer, Double>> toReturn = new HashMap<>();
        Map<String, Timing> currentResults = new HashMap<>(timingsMap);
        switch(type) {
            case TOTAL: {
                for (String key : currentResults.keySet()) {
                    Timing timing = currentResults.get(key);

                    toReturn.put(key, new Tuple<>(timing.calls, timing.average.getTotal()
                            / (double)timing.calls));
                }
                break;
            }
            case AVERAGE: {
                for(String key : currentResults.keySet()) {
                    Timing timing = currentResults.get(key);

                    toReturn.put(key, new Tuple<>(timing.calls, timing.average.getAverage()));
                }
                break;
            }
            case SAMPLES: {
                for(String key : currentResults.keySet()) {
                    Timing timing = currentResults.get(key);

                    toReturn.put(key, new Tuple<>(timing.calls, (double)timing.call));
                }
                break;
            }
            case TICK: {
                int ticks = MathHelper.ceiling_double_int((System.currentTimeMillis() - start) / 50.);

                for (String key : currentResults.keySet()) {
                    Timing timing = currentResults.get(key);

                    toReturn.put(key, new Tuple<>(timing.calls, timing.total / (double)ticks));
                }
                break;
            }
            default: {
                for (String key : currentResults.keySet()) {
                    Timing timing = currentResults.get(key);

                    toReturn.put(key, new Tuple<>(timing.calls, timing.total / (double)timing.calls));
                }
                break;
            }
        }
        return toReturn;
    }

    @Override
    public void stop(String name) {
        long ts = System.currentTimeMillis();
        if(ts - lastReset < 100L) return;
        long extense = System.nanoTime();
        Timing timing = getTiming(name);
        long time = (System.nanoTime() - timing.lastCall) - (System.nanoTime() - extense);
        timing.average.add(time);
        timing.stdDev = Math.abs(time - timing.average.getAverage());
        timing.total+= time;
        timing.call = time;
        timing.calls++;
        totalCalls++;
        lastSample = ts;
    }

    @Override
    public void stop(String name, long extense) {
        long ts = System.currentTimeMillis();
        if(ts - lastReset < 100L) return;
        Timing timing = getTiming(name);
        long time = (System.nanoTime() - timing.lastCall) - (System.nanoTime() - extense);

        timing.average.add(time);
        timing.stdDev = Math.abs(time - timing.average.getAverage());
        timing.total+= time;
        timing.call = time;
        timing.calls++;
        totalCalls++;
        lastSample = ts;
    }

    private Timing getTiming(String name) {
        return timingsMap.computeIfAbsent(name, Timing::new);
    }
}