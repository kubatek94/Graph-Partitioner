package uk.co.kubatek94.partitioner;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import uk.co.kubatek94.graph.V;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 25/04/16.
 */
public class Partition {
    public static int PARTITION_COUNTER = 0;

    private final int id = PARTITION_COUNTER++;
    private int capacity = 0;
    private int size = 0;
    private float use = 0;

    public Partition(int capacity) {
        this.capacity = capacity;
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

    public float getUsePercent() {
        return use;
    }

    public int getUse() { return size; };

    @Override
    public String toString() {
        return String.format("P[%d]: %f Full", id, getUsePercent());
    }
}
