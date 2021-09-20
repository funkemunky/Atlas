package cc.funkemunky.api.profiling;

public class ToggleableProfiler extends BaseProfiler {
    private boolean enabled;

    @Override
    public void start() {
        if(!enabled) return;
        super.start();
    }

    @Override
    public void start(String name) {
        if(!enabled) return;
        super.start(name);
    }

    @Override
    public void stop() {
        if(!enabled) return;
        super.stop();
    }

    @Override
    public void stop(String name) {
        if(!enabled) return;
        super.stop(name);
    }

    @Override
    public void stop(String name, long extense) {
        if(!enabled) return;
        super.stop(name, extense);
    }

    public void setEnabled(boolean enabled) {
        if(this.enabled = enabled) start = System.currentTimeMillis();
        else reset();
    }
}
