package cc.funkemunky.api.utils.objects;

@FunctionalInterface
public interface TriFunction<R, T, U, V> {

    R apply(T t, U u, V v);
}
