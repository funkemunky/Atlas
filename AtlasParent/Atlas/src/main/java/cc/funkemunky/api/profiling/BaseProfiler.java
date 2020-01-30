package cc.funkemunky.api.profiling;

import cc.funkemunky.api.utils.Tuple;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseProfiler implements Profiler {
    public Map<String, Long> timings = new ConcurrentHashMap<>();
    public Map<String, Integer> calls = new ConcurrentHashMap<>();
    public Map<String, Long> stddev = new ConcurrentHashMap<>();
    public Map<String, Long> total = new ConcurrentHashMap<>();
    public Map<String, Tuple<Long, Long>> samples = new ConcurrentHashMap<>();
    public Map<String, List<Long>> samplesTotal = new ConcurrentHashMap<>();
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
        timings.put(name, System.nanoTime());
        calls.put(name, calls.getOrDefault(name, 0) + 1);
        totalCalls++;

        if(start == 0) start = System.currentTimeMillis();
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

    //Returns Tuple<Total Calls, Result>
    @Override
    public Map<String, Tuple<Integer, Double>> results(ResultsType type) {
        Map<String, Tuple<Integer, Double>> toReturn = new HashMap<>();
        switch(type) {
            case TOTAL: {
                double totalTime = System.currentTimeMillis() - start;
                for (String key : total.keySet()) {
                    toReturn.put(key, new Tuple<>(calls.get(key), total.get(key) / totalTime));
                }
                break;
            }
            case AVERAGE: {
                for (String key : samplesTotal.keySet()) {
                    toReturn.put(key, new Tuple<>(calls.get(key), samplesTotal.get(key).stream()
                            .mapToLong(val -> val)
                            .average().orElse(0)));
                }
                break;
            }
            case SAMPLES: {
                for (String key : samples.keySet()) {
                    toReturn.put(key, new Tuple<>(calls.get(key), (double)samples.get(key).one));
                }
                break;
            }
            case TICK: {
                long timeStamp = System.currentTimeMillis();
                samples.keySet().stream()
                        .filter(key -> timeStamp - samples.get(key).two < 60L)
                        .forEach(key -> toReturn.put(key, new Tuple<>(calls.get(key), (double)samples.get(key).one)));
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
        val sample = samples.getOrDefault(name, new Tuple<>(time, System.currentTimeMillis()));

        samples.put(name, new Tuple<>(time, System.currentTimeMillis()));
        stddev.put(name, Math.abs(sample.one - time));

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
    public void stop(String name, long extense) {
        if(System.currentTimeMillis() - lastReset < 100L || !timings.containsKey(name)) return;
        long start = timings.get(name);
        long time = (System.nanoTime() - start) - (System.nanoTime() - extense);
        long lastTotal = total.getOrDefault(name, time);
        val sample = samples.getOrDefault(name, new Tuple<>(time, System.currentTimeMillis()));
        samples.put(name, new Tuple<>(time, System.currentTimeMillis()));

        stddev.put(name, Math.abs(sample.one - time));

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


