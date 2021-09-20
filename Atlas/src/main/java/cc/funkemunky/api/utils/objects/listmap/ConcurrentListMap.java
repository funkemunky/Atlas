package cc.funkemunky.api.utils.objects.listmap;

import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentListMap<K, V> implements ListMap<K, V> {

    private final Map<K, List<V>> values = new ConcurrentHashMap<>();

    @Override
    public List<V> getList(K key) {
        createIfNotExists(key);

        return values.get(key);
    }

    @Override
    public void createIfNotExists(K key) {
        if(!values.containsKey(key))
            values.put(key, new ArrayList<>());
    }

    @Override
    public void add(K key, V value) {
        val list = getList(key);

        list.add(value);

        values.put(key, list);
    }

    @Override
    public int remove(V value) {
        int removed = 0;
        for (K k : values.keySet()) {
            List<V> vals = values.get(k);

            for (V val : vals) {
                if(!val.equals(value)) continue;

                vals.remove(val);
                values.put(k, vals);
                removed++;
            }
        }
        return removed;
    }

    @Override
    public boolean remove(K key, V value) {
        val vals = getList(key);

        boolean removed = vals.remove(value);

        if(removed) {
            values.put(key, vals);
            return true;
        }
        return false;
    }

    @Override
    public List<V> removeKey(K key) {
        return values.remove(key);
    }

    @Override
    public boolean clear() {
        if(values.size() > 0) {
            values.clear();
        }
        return false;
    }

    @Override
    public ContainsResult containsValue(V value) {
        for (K key : values.keySet()) {
            val list = values.get(key);

            for (V v : list) {
                if(value.equals(v)) return new ContainsResult(key, true);
            }
        }
        return new ContainsResult(null, false);
    }

    @Override
    public boolean containsKey(K key) {
        return values.containsKey(key);
    }
}
