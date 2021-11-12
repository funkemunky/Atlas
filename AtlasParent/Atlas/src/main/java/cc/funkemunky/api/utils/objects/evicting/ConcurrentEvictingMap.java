package cc.funkemunky.api.utils.objects.evicting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@RequiredArgsConstructor
public class ConcurrentEvictingMap<K, V> extends ConcurrentSkipListMap<K, V> {

    @Getter
    private final int size;

    @Override
    public V putIfAbsent(K key, V value) {
        if(!value.equals(get(key)))
            checkAndRemove();
        return super.putIfAbsent(key, value);
    }

    @Override
    public V put(K key, V value) {
        checkAndRemove();
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public V remove(Object key) {
        return super.remove(key);
    }

    private boolean checkAndRemove() {
        if(size() >= size) {
            entrySet().remove(firstEntry());
            return true;
        }
        return false;
    }
}
