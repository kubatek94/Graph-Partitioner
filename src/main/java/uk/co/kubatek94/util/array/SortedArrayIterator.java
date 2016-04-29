package uk.co.kubatek94.util.array;

import java.util.Iterator;

/**
 * Created by kubatek94 on 29/04/16.
 */
public class SortedArrayIterator<T> implements Iterator<T> {
    protected SortedArray<T> sortedArray;
    protected int i = 0;

    public SortedArrayIterator(SortedArray<T> sortedArray) {
        this.sortedArray = sortedArray;
        this.i = sortedArray.leftIndex;
        this.sortedArray.resort();
    }

    public boolean hasPrev() {
        return i > sortedArray.leftIndex;
    }

    public T prev() {
        return sortedArray.elements[i--];
    }

    @Override
    public boolean hasNext() {
        return i < sortedArray.rightIndex;
    }

    @Override
    public T next() {
        return sortedArray.elements[i++];
    }

    public T current() {
        return sortedArray.elements[i];
    }
}
