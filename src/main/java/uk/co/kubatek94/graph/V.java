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
public class V /*implements Comparable<V>*/ {
    private String id = null;
    private int partition = -1;
    private Set<Integer> partitions = null;
    private ConcurrentLinkedQueue<V> neighs = null;
    private AtomicInteger size = new AtomicInteger(0);

    public V(String id) {
        this.id = id;
        init();
    }

    public void init() {
        this.neighs = new ConcurrentLinkedQueue<>();
        this.partitions = new HashSet<>();
        this.size = new AtomicInteger(0);
    }

    public V addNeighbour(V neighbour) {
        neighs.add(neighbour);
        size.incrementAndGet();
        //neighs.put(neighbour.id(), neighbour);
        return this;
    }

    public V removeNeighbour(V neighbour) {
        neighs.remove(neighbour);
        size.decrementAndGet();
        //neighs.remove(neighbour.id());
        return this;
    }

    /*public ConcurrentSkipListSet<V> neighs() {
        return neighs;
    }*/
    /*public ConcurrentHashMap<String, V> neighs() {
        return neighs;
    }*/
    public ConcurrentLinkedQueue<V> neighs() {
        return neighs;
    }

    public Integer size() {
        //return neighs.size();
        return size.intValue();
    }

    public String id() {
        return id;
    }

    public Set<Integer> partitions() {
        return partitions;
    }

    public int partition() {
        return partition;
    }

    public V partition(int p) {
        this.partition = p;
        this.partitions.add(p);
        return this;
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

    /*@Override
    public int compareTo(V o) {
        return (partition == o.partition()) ? -1 : (new Integer(partition).compareTo(o.partition()));
        //return (partition == o.partition()) ? id.compareTo(o.id()) : (new Integer(partition).compareTo(o.partition()));
    }*/
}
