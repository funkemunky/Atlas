package cc.funkemunky.api.utils.menu.util;

import java.util.Iterator;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 3/28/2018
 * <p>
 * This class allows us to fulfill requests for {@link Menu}'s
 * Iterable need since default Java arrays do not have a built-in iterator implementation
 */
public class ArrayIterator<T> implements Iterator<T> {

    private final T[] elementArray;
    private int currentIndex;

    public ArrayIterator(T[] elementArray) {
        this.elementArray = elementArray;
    }

    @Override
    public boolean hasNext() {
        return elementArray.length > currentIndex;
    }

    @Override
    public T next() {
        return elementArray[currentIndex++];
    }
}
