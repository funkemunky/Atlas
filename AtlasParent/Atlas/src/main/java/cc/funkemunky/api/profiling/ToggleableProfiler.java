package cc.funkemunky.api.profiling;

public class ToggleableProfiler extends BaseProfiler {
    public boolean enabled;

    @Override
    public synchronized void start() {
        if(!enabled) return;
        super.start();
    }

    @Override
    public synchronized void start(String name) {
        if(!enabled) return;
        super.start(name);
    }

    @Override
    public synchronized void stop() {
        if(!enabled) return;
        super.stop();
    }

    @Override
    public synchronized void stop(String name) {
        if(!enabled) return;
        super.stop(name);
    }

    @Override
    public synchronized void stop(String name, long extense) {
        if(!enabled) return;
        super.stop(name, extense);
    }
}
