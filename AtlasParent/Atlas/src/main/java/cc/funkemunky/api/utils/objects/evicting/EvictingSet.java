/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 * Modified by funkemunky.
 */

package cc.funkemunky.api.utils.objects.evicting;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class EvictingSet<T> extends HashSet<T> {
    private int maxSize;

    public EvictingSet(int maxSize) {
        this.maxSize = maxSize;
    }

    public EvictingSet(Collection<? extends T> c, int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= maxSize) remove(0);
        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return c.stream().anyMatch(this::add);
    }

    @Override
    public Stream<T> stream() {
        return new CopyOnWriteArrayList<>(this).stream();
    }
}