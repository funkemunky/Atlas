package cc.funkemunky.api.event.system;

@Deprecated
public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean var1);
}

