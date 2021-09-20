package cc.funkemunky.api.utils;

import java.util.Iterator;

public class Step {

    public static GenericStepper<Float> step(float start, float incr, float end) {
        Link<Float> first = new Link<>();
        Link<Float> cur = first;
        Link<Float> last = first;

        first.value = start;
        while (start < end) {
            last = cur;
            cur = new Link<>();
            cur.value = start+=incr;
            last.next = cur;
        }
        cur = new Link<>();
        cur.value =end;
        last.next = cur;
        return new GenericStepper<>(first);
    }

    public static GenericStepper<Double> step(double start, double incr, double end) {
        Link<Double> first = new Link<>();
        Link<Double> cur = first;
        Link<Double> last = first;
        first.value = start;
        while (start < end) {
            last = cur;
            cur = new Link<>();
            start+=incr;
            cur.value = start;
            last.next = cur;
        }
        cur = new Link<>();
        cur.value = end;
        last.next = cur;
        return new GenericStepper<>(first);
    }

    public static class GenericStepper<T> implements Iterator<T>, Iterable<T> {

        private Link<T> first;
        private Link<T> link;

        public GenericStepper(Link<T> link) {
            this.link = link;
            this.first = link;
        }

        @Override
        public boolean hasNext() {
            return link!=null;
        }

        @Override
        public T next() {
            T v = link.value;
            link = link.next;
            return v;
        }

        public boolean first() {
            return link == first || link == first.next;
        }

        public boolean last() {
            return link==null;
        }

        public int count_() {
            int i =0;
            Link<T> t = first;
            while (t!=null) {
                i++;
                t = t.next;
            }
            return i;
        }

        public void reset() {
            this.link = first;
        }

        @Override
        public Iterator<T> iterator() {
            reset();
            return this;
        }
    }

    private static class Link<T> {
        private T value;
        private Link<T> next;
    }

}
