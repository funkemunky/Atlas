package cc.funkemunky.api.utils.objects;

import cc.funkemunky.api.utils.Tuple;

@FunctionalInterface
public interface BiSupplier<T, V> {

    Tuple<T, V> get();
}
