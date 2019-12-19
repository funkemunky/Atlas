package cc.funkemunky.api.profiling;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
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
        Atlas.getInstance().getSchedular().scheduleAtFixedRate(() -> {
            for (String name : samplesPerTick.keySet()) {

                long avg = new ArrayList<>(samplesPerTick.getOrDefault(name, new CopyOnWriteArrayList<>())).stream()
                        .mapToLong(val -> val)
                        .sum();

                averageSamples.put(name, avg);
                samplesPerTick.put(name, new CopyOnWriteArrayList<>());
            }
        }, 50L, 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized void start() {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        start(stack.getMethodName());
    }

    @Override
    public synchronized void start(String name) {
        timings.put(name, System.nanoTime());
        calls.put(name, calls.getOrDefault(name, 0) + 1);
        totalCalls++;
    }

    @Override
    public synchronized void stop() {
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

    //Returns Tuple<Total Calls, Result>
    @Override
    public Map<String, Tuple<Integer, Double>> results(ResultsType type) {
        Map<String, Tuple<Integer, Double>> toReturn = new HashMap<>();
        switch(type) {
            case TOTAL: {
                for (String key : total.keySet()) {
                    toReturn.put(key, new Tuple<>(calls.get(key), total.get(key) * ((double) calls.get(key) / totalCalls)));
                }
                break;
            }
            case AVERAGE: {
                for (String key : samplesTotal.keySet()) {
                    toReturn.put(key, new Tuple<>(calls.get(key), samplesTotal.get(key).stream().mapToLong(val -> val).average().orElse(0)));
                }
                break;
            }
            case SAMPLES: {
                for (String key : samples.keySet()) {
                    toReturn.put(key, new Tuple<>(calls.get(key), (double)samples.get(key)));
                }
                break;
            }
            case TICK: {
                for(String key : averageSamples.keySet()) {
                    toReturn.put(key, new Tuple<>(calls.get(key), (double)averageSamples.get(key)));
                }
                break;
            }
        }
        return toReturn;
    }

    @Override
    public synchronized void stop(String name) {
        if(System.currentTimeMillis() - lastReset < 100L || !timings.containsKey(name)) return;
        long extense = System.nanoTime();
        long start = timings.get(name);
        long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
        long lastTotal = total.getOrDefault(name, time);
        long sample = samples.getOrDefault(name, time);

        samples.put(name, time);
        List<Long> sList = this.samplesPerTick.getOrDefault(name, new CopyOnWriteArrayList<>());

        if(sList.size() > 100) {
            sList.clear();
        } else sList.add(time);

        samplesPerTick.put(name, sList);
        stddev.put(name, Math.abs(sample - time));

        List<Long> samplesTotal = this.samplesTotal.getOrDefault(name, new CopyOnWriteArrayList<>());

        if(samplesTotal.size() > 1000) {
            samplesTotal.remove(0);
        }
        samplesTotal.add(time);
        this.samplesTotal.put(name, samplesTotal);

        total.put(name, lastTotal + time);
        lastSample = System.currentTimeMillis();
    }

    @Override
    public synchronized void stop(String name, long extense) {
        if(System.currentTimeMillis() - lastReset < 100L || !timings.containsKey(name)) return;
        long start = timings.get(name);
        long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
        long lastTotal = total.getOrDefault(name, time);
        long sample = samples.getOrDefault(name, time);
        samples.put(name, time);
        List<Long> sList = this.samplesPerTick.getOrDefault(name, new CopyOnWriteArrayList<>());

        if(sList.size() > 100) {
            sList.clear();
        } else sList.add(time);

        samplesPerTick.put(name, sList);
        stddev.put(name, Math.abs(sample - time));

        List<Long> samplesTotal = this.samplesTotal.getOrDefault(name, new CopyOnWriteArrayList<>());

        if(samplesTotal.size() > 1000) {
            samplesTotal.remove(0);
        }
        samplesTotal.add(time);
        this.samplesTotal.put(name, samplesTotal);

        total.put(name, lastTotal + time);
        lastSample = System.currentTimeMillis();
    }


}


