package cc.funkemunky.api.utils.objects.evicting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ConcurrentEvictingMap<K, V> extends ConcurrentHashMap<K, V> {

    @Getter
    private final int size;

    private final Deque<K> storedKeys = new LinkedList<>();

    @Override
    public boolean remove(Object key, Object value) {
        //noinspection SuspiciousMethodCalls
        storedKeys.remove(key);
        return super.remove(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if(!storedKeys.contains(key) || !get(key).equals(value))
            checkAndRemove();
        return super.putIfAbsent(key, value);
    }

    @Override
    public V put(K key, V value) {
        checkAndRemove();
        storedKeys.addLast(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        storedKeys.clear();
        super.clear();
    }

    @Override
    public V remove(Object key) {
        //noinspection SuspiciousMethodCalls
        storedKeys.remove(key);
        return super.remove(key);
    }

    private boolean checkAndRemove() {
        if(storedKeys.size() >= size) {
            storedKeys.removeFirst();
            return true;
        }
        return false;
    }
}
