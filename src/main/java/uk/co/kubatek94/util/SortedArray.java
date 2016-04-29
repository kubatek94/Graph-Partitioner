package uk.co.kubatek94.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by kubatek94 on 29/04/16.
 * SortedArray provides good performance for data which gets added once and then only gets reduced.
 *
 * It uses comparator to sort the elements first, so that it can use binary search later to find the elements quickly.
 * Important: Comparator must return a single exact match, so that each element can be identified uniquely.
 * Otherwise binary search won't work, as it does not provide guarantee to return first matching element if they are equal in the comparator.
 *
 * It provides methods to remove elements from the beginning and the end of the array (removeFirst, removeLast).
 * It does not resize the array. It does not provide boundary checks.
 */
public class SortedArray<T> {
    private T elements[];
    private Comparator<T> comparator;

    private int leftIndex = 0;
    private int rightIndex = 0;

    public SortedArray(int size, Comparator<T> comparator) {
        this.elements = (T[]) new Object[size];
        this.comparator = comparator;
    }

    public SortedArray(Collection<T> elements, Comparator<T> comparator) {
        this(elements.size(), comparator);
        addAll(elements);
    }

    public void addAll(Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            elements[rightIndex] = iterator.next();
            rightIndex++;
        }
        Arrays.sort(elements, comparator);
    }

    public T get(int index) {
        return elements[leftIndex + index];
    }

    public int size() {
        return rightIndex - leftIndex;
    }

    public T removeFirst() {
        T element = elements[leftIndex++];
        return element;
    }

    public T removeLast() {
        T element = elements[--rightIndex];
        return element;
    }

    public T remove(int index) {
        index = leftIndex + index;

        if (index < 0) {
            System.out.format("Found index %d\n", index);
            return null;
        } else {
            T element = elements[index];

            //find index of the middle of the data
            int middle = ((leftIndex+rightIndex) / 2);

            //if our element is closer to the right side of the array
            if (index > middle) {
                //shift elements past the index to the left
                System.arraycopy(elements, index + 1, elements, index, rightIndex - index - 1);
                rightIndex--;

                //else our element is closer to the left side of the array
            } else {
                //shift elements before the index to the right
                System.arraycopy(elements, leftIndex, elements, leftIndex + 1, index - leftIndex);
                leftIndex++;
            }

            return element;
        }
    }

    public void remove(T element) {
        int index = findIndex(element);
        remove(index);
    }

    public int findIndex(T element) {
        return Arrays.binarySearch(elements, leftIndex, rightIndex, element, comparator) - leftIndex;
    }
}
