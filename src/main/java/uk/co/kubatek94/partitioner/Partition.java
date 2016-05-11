package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.util.array.SortedArray;
import uk.co.kubatek94.util.array.SortedArrayIterator;
import uk.co.kubatek94.util.array.SortedDynamicArray;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by kubatek94 on 25/04/16.
 */
public class Partition {
    private final int id;

    protected int capacity = 0;
    protected int targetFraction = 0;

    protected int size = 0;
    protected float use = 0;

    public Partition(int id, int capacity, int targetFraction) {
        this.id = id;
        this.capacity = capacity;
        this.targetFraction = targetFraction;
    }

    public int id() {
        return id;
    }

    public boolean addVertex(V vertex) {
        if (size < capacity) {
            vertex.partition(id);
            size++;
            use = ((float) size / capacity);
            return true;
        }

        return false;
    }

    public float getFractionUse() {
        return (float) size / targetFraction;
    }

    public float getUse() {
        return use;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return String.format("P[%d]: %f Full", id, getUse());
    }
}
