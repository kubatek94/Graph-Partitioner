package uk.co.kubatek94.util.array;

import uk.co.kubatek94.util.array.SortedArray;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * Created by kubatek94 on 29/04/16.
 */
public class SortedDynamicArray<T> extends SortedArray<T> {
    public SortedDynamicArray(int size, Comparator<T> comparator) {
        super(size, comparator);
    }

    public SortedDynamicArray(Collection<T> elements, Comparator<T> comparator) {
        super(elements, comparator);
    }

    public void add(T element) {
        elements[rightIndex] = element;
        rightIndex++;
    }

    public T getFirst() {
        resort();
        return elements[leftIndex];
    }

    public T getLast() {
        resort();
        return elements[rightIndex-1];
    }

    @Override
    public int get(T element) {
        resort();
        return super.get(element);
    }

    @Override
    public T removeFirst() {
        resort();
        return super.removeFirst();
    }

    @Override
    public T removeLast() {
        resort();
        return super.removeLast();
    }
}
