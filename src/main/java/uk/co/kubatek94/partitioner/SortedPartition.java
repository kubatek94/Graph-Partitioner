package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.V;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by kubatek94 on 30/04/16.
 */
public class SortedPartition extends Partition {
    private final SortedSet<V> vertices;

    public SortedPartition(int id, int capacity, int targetFraction) {
        super(id, capacity, targetFraction);

        this.vertices = new TreeSet<>((a, b) -> {
            //sort by the degree first, then by the id if they have equal size
            int result = b.size().compareTo(a.size());
            if (result == 0) {
                return b.id().compareTo(a.id());
            }
            return result;
        });
    }

    @Override
    public boolean addVertex(V vertex) {
        if (super.addVertex(vertex)) {
            vertices.add(vertex);
            return true;
        }

        return false;
    }

    public Iterator<V> iterator() {
        return vertices.iterator();
    }
}
