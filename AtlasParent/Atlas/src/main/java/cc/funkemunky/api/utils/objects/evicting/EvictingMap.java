package cc.funkemunky.api.utils.objects.evicting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class EvictingMap<K, V> extends LinkedHashMap<K, V> {

    @Getter
    private final int size;

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() >= size;
    }
}
