package cc.funkemunky.api.utils.objects.listmap;

import lombok.Getter;

import java.util.List;

public interface ListMap<K, V> {

    List<V> getList(K key);

    void createIfNotExists(K key);

    void add(K key, V value);

    int remove(V value);

    boolean remove(K key, V value);

    List<V> removeKey(K key);

    boolean clear();

    ContainsResult containsValue(V value);

    boolean containsKey(K key);

    @Getter
    class ContainsResult {
        private boolean result;
        private Object key;
        protected ContainsResult(Object key, boolean result) {
            this.key = key;
            this.result = result;
        }
    }
}
