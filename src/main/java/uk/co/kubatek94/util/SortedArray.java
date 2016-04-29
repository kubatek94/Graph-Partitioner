package uk.co.kubatek94.util;

import uk.co.kubatek94.graph.V;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by kubatek94 on 29/04/16.
 */
public class SortedArray<T> {
    private T vertices[];

    private int leftIndex = 0;
    private int rightIndex = 0;

    private Comparator<T> comparator;

    public SortedArray(int size, Comparator<T> comparator) {
        this.vertices = (T[]) new Object[size];
        this.comparator = comparator;
    }

    public SortedArray(Collection<T> elements, Comparator<T> comparator) {
        this(elements.size(), comparator);
        addAll(elements);
    }

    public void addAll(Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            vertices[rightIndex] = iterator.next();
            rightIndex++;
        }
        Arrays.sort(vertices, comparator);
    }

    public T get(int index) {
        return vertices[leftIndex + index];
    }

    public int size() {
        return rightIndex - leftIndex;
    }

    public T removeFirst() {
        T vertex = vertices[leftIndex++];
        return vertex;
    }

    public T removeLast() {
        T vertex = vertices[--rightIndex];
        return vertex;
    }

    public T remove(int index) {
        index = leftIndex + index;

        if (index < 0) {
            System.out.format("Found index %d\n", index);
            return null;
        } else {
            T element = vertices[index];

            //find index of the middle of the data
            int middle = ((leftIndex+rightIndex) / 2);

            //if our element is closer to the right side of the array
            if (index > middle) {
                //shift elements past the index to the left
                System.arraycopy(vertices, index + 1, vertices, index, rightIndex - index - 1);
                rightIndex--;

                //else our element is closer to the left side of the array
            } else {
                //shift elements before the index to the right
                System.arraycopy(vertices, leftIndex, vertices, leftIndex + 1, index - leftIndex);
                leftIndex++;
            }

            return element;
        }
    }

    public void remove(T vertex) {
        int index = findIndex(vertex);
        remove(index);
    }

    public int findIndex(T vertex) {
        return Arrays.binarySearch(vertices, leftIndex, rightIndex, vertex, comparator) - leftIndex;
    }
}
