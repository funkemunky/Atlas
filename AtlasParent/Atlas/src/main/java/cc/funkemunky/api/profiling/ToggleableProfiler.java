package cc.funkemunky.api.profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ToggleableProfiler implements Profiler {
    public Map<String, Long> timings = new HashMap<>();
    public Map<String, Integer> calls = new HashMap<>();
    public Map<String, Long> stddev = new HashMap<>();
    public Map<String, Long> total = new HashMap<>();
    public Map<String, Long> samples = new HashMap<>();
    public Map<String, List<Long>> samplesTotal = new HashMap<>();
    public long lastSample = 0;
    public int totalCalls = 0;
    public boolean enabled;

    public ToggleableProfiler() {
        enabled = false;
    }

    @Override
    public void start() {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        start(stack.getMethodName());
    }

    @Override
    public void start(String name) {
        if (enabled) {
            timings.put(name, System.nanoTime());
            calls.put(name, calls.getOrDefault(name, 0) + 1);
            totalCalls++;
        }
    }

    @Override
    public void stop() {
        long extense = System.nanoTime();
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        stop(stack.getMethodName(), extense);
    }

    @Override
    public Map<String, Double> results(ResultsType type) {
        Map<String, Double> toReturn = new HashMap<>();
        switch(type) {
            case TOTAL: {
                for (String key : total.keySet()) {
                    toReturn.put(key, total.get(key) / (double) calls.get(key));
                }
                break;
            }
            case AVERAGE: {
                for (String key : samplesTotal.keySet()) {
                    toReturn.put(key, samplesTotal.get(key).stream().mapToLong(val -> val).average().orElse(0));
                }
                break;
            }
            case SAMPLES: {
                for (String key : samples.keySet()) {
                    toReturn.put(key, (double)samples.get(key));
                }
                break;
            }
        }
        return toReturn;
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
    public void stop(String name) {
        if (enabled) {
            long extense = System.nanoTime();
            long start = timings.get(name);
            long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
            long lastTotal = total.getOrDefault(name, time);
            long sample = samples.getOrDefault(name, time);

            samples.put(name, time);
            stddev.put(name, Math.abs(sample - time));

            List<Long> samplesTotal = this.samplesTotal.getOrDefault(name, new ArrayList<>());

            samplesTotal.add(time);
            this.samplesTotal.put(name, samplesTotal);

            total.put(name, lastTotal + time);
            lastSample = System.currentTimeMillis();
        }
    }

    @Override
    public void stop(String name, long extense) {
        if (enabled) {
            long start = timings.get(name);
            long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
            long lastTotal = total.getOrDefault(name, time);
            long sample = samples.getOrDefault(name, time);

            samples.put(name, time);
            stddev.put(name, Math.abs(sample - time));

            List<Long> samplesTotal = this.samplesTotal.getOrDefault(name, new ArrayList<>());

            samplesTotal.add(time);
            this.samplesTotal.put(name, samplesTotal);

            total.put(name, lastTotal + time);
            lastSample = System.currentTimeMillis();
        }
    }
}
