package cc.funkemunky.api.utils;


import cc.funkemunky.api.Atlas;

public class TickTimer {
    private int ticks = Atlas.getInstance().getCurrentTicks(), defaultPassed;

    public TickTimer(int defaultPassed) {
        this.defaultPassed = defaultPassed;
    }

    public void reset() {
        ticks = Atlas.getInstance().getCurrentTicks();
    }

    public boolean hasPassed() {
        return Atlas.getInstance().getCurrentTicks() - ticks > defaultPassed;
    }

    public boolean hasPassed(int amount) {
        return Atlas.getInstance().getCurrentTicks() - ticks > amount;
    }

    public boolean hasNotPassed() {
        return Atlas.getInstance().getCurrentTicks() - ticks <= defaultPassed;
    }

    public boolean hasNotPassed(int amount) {
        return Atlas.getInstance().getCurrentTicks() - ticks <= amount;
    }

    public int getPassed() {
        return Atlas.getInstance().getCurrentTicks() - ticks;
    }
}
