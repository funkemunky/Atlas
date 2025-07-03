package cc.funkemunky.api.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class ExpTimer {
    public final Player player;
    public long timerEnd;
    public final long extense;
    public final Runnable onComplete;
    public BukkitTask levelTask, expTask;
    private int previousLevel;
    private float previousExp;

    public ExpTimer(Player player, long time, TimeUnit unit, Runnable onComplete) {
        this.player = player;
        this.extense = unit.toMillis(time);
        this.timerEnd = System.currentTimeMillis() + extense;
        this.onComplete = onComplete;

        previousExp = player.getExp();
        previousLevel = player.getLevel();
        levelTask = RunUtils.taskTimer(() -> {
            if(getMillisLeft() <= 0) {
                levelTask.cancel();
                levelTask = null;
                return;
            }
            
            player.setLevel((int)Math.floor(getMillisLeft() / 1000D));
        }, 2, 20);
        expTask = RunUtils.taskTimer(() -> {
            if(getMillisLeft() <= 0 && levelTask == null) {
                expTask.cancel();
                expTask = null;
                player.setLevel(previousLevel);
                player.setExp(previousExp);
                onComplete.run();
                return;
            }

            player.setExp(getMillisLeft() / (float)extense);
        }, 2, 2);
    }

    private long getMillisLeft() {
        return timerEnd - System.currentTimeMillis();
    }

    public void stop() {
        timerEnd = System.currentTimeMillis();
    }

    public void cancel() {
        levelTask.cancel();
        expTask.cancel();
        levelTask = expTask = null;
        player.setExp(previousExp);
        player.setLevel(previousLevel);
    }
}
