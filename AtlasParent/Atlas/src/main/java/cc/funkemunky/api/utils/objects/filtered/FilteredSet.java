package cc.funkemunky.api.utils.objects.filtered;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilteredSet<T> extends HashSet<T> {

    private Predicate<T> predicate;

    public FilteredSet(Predicate<T> predicate) {
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

