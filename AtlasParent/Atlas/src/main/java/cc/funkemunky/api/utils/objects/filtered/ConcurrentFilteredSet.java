package cc.funkemunky.api.utils.objects.filtered;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConcurrentFilteredSet<T> extends ConcurrentSkipListSet<T> {

    private Predicate<T> predicate;

    public ConcurrentFilteredSet(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean add(T t) {
        if(predicate.test(t)) {
            return super.add(t);
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        List<? extends T> filtered = c.stream().filter(predicate).collect(Collectors.toList());

        return super.addAll(filtered);
    }
}

