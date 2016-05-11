package uk.co.kubatek94.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kubatek94 on 25/04/16.
 */
public class V {
    private String id = null;
    private Set<Integer> partitions = null;
    private boolean outOfStream = false;
    private ConcurrentHashMap<String, V> neighbours = null;

    public V(String id) {
        this.id = id;
        this.neighbours = new ConcurrentHashMap<>();
        this.partitions = new HashSet<>();
    }

    public V addNeighbour(V neighbour) {
        neighbours.put(neighbour.id(), neighbour);
        return this;
    }

    public V removeNeighbour(V neighbour) {
        neighbours.remove(neighbour.id());
        return this;
    }

    public boolean hasNeighbour(V vertex) {
        return neighbours.get(vertex.id()) != null;
    }

    public ConcurrentHashMap<String, V> neighbours() {
        return neighbours;
    }

    public Integer size() {
        return neighbours.size();
    }

    public String id() {
        return id;
    }

    public Set<Integer> partitions() {
        return partitions;
    }

    public V partition(int p) {
        partitions.add(p);
        return this;
    }

    public V outOfStream(boolean outOfStream) {
        this.outOfStream = outOfStream;
        return this;
    }

    public boolean isOutOfStream() {
        return outOfStream;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof V) && ((V)o).id().equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return String.format("V[%s]", id);
    }
}
