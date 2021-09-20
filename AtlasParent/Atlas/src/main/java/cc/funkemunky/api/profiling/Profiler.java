package cc.funkemunky.api.profiling;

import cc.funkemunky.api.utils.Tuple;

import java.util.Map;

public interface Profiler {
    void start(String name);

    void start();

    void stop(String name, long extense);

    void stop(String name);

    void stop();

    void reset();

    Map<String, Tuple<Integer, Double>> results(ResultsType type);
}
