package cc.funkemunky.api.event.system;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean var1);
}

