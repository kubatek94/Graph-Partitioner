package uk.co.kubatek94.util.array;

import java.util.Comparator;

/**
 * Created by kubatek94 on 29/04/16.
 */
public class SortedUpdatableArray<T extends UpdatableElement<T>> extends SortedArray<T> {

    public SortedUpdatableArray(int size, Comparator<T> comparator) {
        super(size, comparator);
    }

    public T getFirst() {
        return null;
    }
}
