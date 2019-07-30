package cc.funkemunky.api.profiling;

import cc.funkemunky.api.Atlas;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BaseProfiler implements Profiler {
    public Map<String, Long> timings = new HashMap<>();
    public Map<String, Integer> calls = new HashMap<>();
    public Map<String, Long> stddev = new HashMap<>();
    public Map<String, Long> total = new HashMap<>();
    public Map<String, Long> samples = new HashMap<>();
    public Map<String, Long> averageSamples = new HashMap<>();
    public Map<String, List<Long>> samplesPerTick = new HashMap<>();
    public Map<String, List<Long>> samplesTotal = new HashMap<>();
    public long lastSample = 0, lastReset;
    public int totalCalls = 0;

    public BaseProfiler() {
        Atlas.getInstance().getSchedular().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (String name : samplesPerTick.keySet()) {

                    long avg = new ArrayList<>(samplesPerTick.getOrDefault(name, new ArrayList<>())).stream()
                            .mapToLong(val -> val)
                            .sum();

                    averageSamples.put(name, avg);
                    samplesPerTick.put(name, new ArrayList<>());
                }
            }
        }, 50L, 50L, TimeUnit.MILLISECONDS);
    }

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
        if(System.currentTimeMillis() - lastReset < 100L) return;
        long extense = System.nanoTime();
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        stop(stack.getMethodName(), extense);
    }

    @Override
    public void reset() {
        lastReset = System.currentTimeMillis();
        lastSample = totalCalls = 0;
        timings.clear();
        calls.clear();
        stddev.clear();
        total.clear();
        samples.clear();
    }

    @Override
    public Map<String, Double> results(ResultsType type) {
        /*if(type.equals(ResultsType.TOTAL)) {
            return total.keySet().parallelStream().collect(Collectors.toMap(key -> key, key -> {
                long totalMS = total.get(key);
                int totalCalls = calls.get(key);
                return totalMS / totalCalls / 1000000D;
            }));
        } else {
            return samples.keySet().parallelStream().collect(Collectors.toMap(key -> key, key -> samples.get(key) / 1000000D));
        }*/
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
            case TICK: {
                for(String key : averageSamples.keySet()) {
                    toReturn.put(key, (double)averageSamples.get(key));
                }
                break;
            }
        }
        return toReturn;
    }

    @Override
    public void stop(String name) {
        if(System.currentTimeMillis() - lastReset < 100L) return;
        long extense = System.nanoTime();
        long start = timings.get(name);
        long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
        long lastTotal = total.getOrDefault(name, time);
        long sample = samples.getOrDefault(name, time);

        samples.put(name, time);
        List<Long> sList = this.samplesPerTick.getOrDefault(name, new ArrayList<>());

        sList.add(time);

        samplesPerTick.put(name, sList);
        stddev.put(name, Math.abs(sample - time));

        List<Long> samplesTotal = this.samplesTotal.getOrDefault(name, new ArrayList<>());

        samplesTotal.add(time);
        this.samplesTotal.put(name, samplesTotal);

        total.put(name, lastTotal + time);
        lastSample = System.currentTimeMillis();
    }

    @Override
    public void stop(String name, long extense) {
        if(System.currentTimeMillis() - lastReset < 100L) return;
        long start = timings.get(name);
        long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
        long lastTotal = total.getOrDefault(name, time);
        long sample = samples.getOrDefault(name, time);
        samples.put(name, time);
        List<Long> sList = this.samplesPerTick.getOrDefault(name, new ArrayList<>());

        sList.add(time);

        samplesPerTick.put(name, sList);

        stddev.put(name, Math.abs(sample - time));

        List<Long> samplesTotal = this.samplesTotal.getOrDefault(name, new ArrayList<>());

        samplesTotal.add(time);
        this.samplesTotal.put(name, samplesTotal);

        total.put(name, lastTotal + time);
        lastSample = System.currentTimeMillis();
    }


}


