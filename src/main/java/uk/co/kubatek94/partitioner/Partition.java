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
    public static int PARTITION_COUNTER = 0;
    private final int id = PARTITION_COUNTER++;

    private int capacity = 0;
    private int targetFraction = 0;

    private int size = 0;
    private float use = 0;

    private final SortedSet<V> vertices;

    public Partition(int capacity, int targetFraction) {
        this.capacity = capacity;
        this.targetFraction = targetFraction;

        this.vertices = new TreeSet<>((a, b) -> {
            //sort by the degree first, then by the id if they have equal size
            int result = b.size().compareTo(a.size());
            if (result == 0) {
                return b.id().compareTo(a.id());
            }
            return result;
        });
    }

    public int id() {
        return id;
    }

    public boolean addVertex(V vertex) {
        if (size < capacity) {
            vertex.partition(id);
            vertices.add(vertex);
            size++;

            use = ((float) size / capacity);
            return true;
        } else {
            System.out.println("No more..");
        }

        return false;
    }

    public Iterator<V> iterator() {
        return vertices.iterator();
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
