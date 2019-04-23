package cc.funkemunky.api.profiling;

public interface Profiler {
    void start(String name);

    void start();

    void stop(String name, long extense);

    void stop(String name);

    void stop();

    void reset();
<<<<<<< HEAD

    Map<String, Double> results(ResultsType type);
=======
>>>>>>> parent of 71b8b14... New Atlas Improvements (I hope)
}
