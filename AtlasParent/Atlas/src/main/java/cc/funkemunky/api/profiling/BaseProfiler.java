package cc.funkemunky.api.profiling;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseProfiler implements Profiler {
    public Map<String, Long> timings = new HashMap<>();
    public Map<String, Integer> calls = new HashMap<>();
    public Map<String, Long> stddev = new HashMap<>();
    public Map<String, Long> total = new HashMap<>();
    public Map<String, Long> samples = new HashMap<>();
    public long lastSample = 0;
    public int totalCalls = 0;

    @Override
    public void start() {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        start(stack.getMethodName());
    }

    @Override
    public void start(String name) {
        timings.put(name, System.nanoTime());
        calls.put(name, calls.getOrDefault(name, 0) + 1);
        totalCalls++;
    }

    @Override
    public void stop() {
        long extense = System.nanoTime();
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        stop(stack.getMethodName(), extense);
    }

    @Override
    public void reset() {
        lastSample = totalCalls = 0;
        timings.clear();
        calls.clear();
        stddev.clear();
        total.clear();
        samples.clear();
    }

    @Override
    public Map<String, Double> results(ResultsType type) {
        if(type.equals(ResultsType.TOTAL)) {
            return total.keySet().parallelStream().collect(Collectors.toMap(key -> key, key -> {
                long totalMS = total.get(key);
                int totalCalls = calls.get(key);
                return totalMS / totalCalls / 1000000D;
            }));
        } else {
            return samples.keySet().parallelStream().collect(Collectors.toMap(key -> key, key -> samples.get(key) / 1000000D));
        }
    }

    @Override
    public void stop(String name) {
        long extense = System.nanoTime();
        long start = timings.get(name);
        long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
        long lastTotal = total.getOrDefault(name, time);
        long sample = samples.getOrDefault(name, time);

        samples.put(name, time);
        stddev.put(name, Math.abs(sample - time));

        total.put(name, lastTotal + time);
        lastSample = System.currentTimeMillis();
    }

    @Override
    public void stop(String name, long extense) {
        long start = timings.get(name);
        long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
        long lastTotal = total.getOrDefault(name, time);
        long sample = samples.getOrDefault(name, time);
        samples.put(name, time);

        stddev.put(name, Math.abs(sample - time));

        total.put(name, lastTotal + time);
        lastSample = System.currentTimeMillis();
    }


}


