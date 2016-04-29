package uk.co.kubatek94.graph;

import uk.co.kubatek94.order.RandomStreamOrder;
import uk.co.kubatek94.order.StreamOrder;
import uk.co.kubatek94.partitioner.GraphPartitioner;
import uk.co.kubatek94.partitioner.Partition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 25/04/16.
 */
public class G {
    private ConcurrentHashMap<String, V> vertices = null;
    private StreamOrder streamOrder = null;
    private GraphPartitioner graphPartitioner = null;

    public G() {
        this(null);
    }

    public G(StreamOrder streamOrder) {
        vertices = new ConcurrentHashMap<>();
        if (streamOrder == null) {
            streamOrder = new RandomStreamOrder();
        }
        this.streamOrder = streamOrder;
    }

    public G addVertex(V vertex) {
        vertices.putIfAbsent(vertex.id(), vertex);
        return this;
    }

    public G removeVertex(V vertex) {
        vertices.remove(vertex.id(), vertex);
        return this;
    }

    public Map<String, V> vertices() {
        return vertices;
    }

    public G addEdge(E edge) {
        V first = new V(edge.first);
        V second = new V(edge.second);

        V previousFirst = vertices.putIfAbsent(edge.first, first);
        V previousSecond = vertices.putIfAbsent(edge.second, second);

        if (previousFirst != null) {
            first = previousFirst;
        }

        if (previousSecond != null) {
            second = previousSecond;
        }

        //connect two vertices
        first.addNeighbour(second);
        second.addNeighbour(first);

        return this;
    }

    public GraphPartitioner partitioner() {
        return graphPartitioner;
    }

    public G partition(GraphPartitioner graphPartitioner) {
        this.graphPartitioner = graphPartitioner;
        graphPartitioner.partition(this);
        return this;
    }

    public Stream<V> stream() {
        this.streamOrder.setGraph(this);
        return Stream.generate(this.streamOrder).sequential().limit(vertices.size());
    }

    public long usedMemory() {
        Runtime instance = Runtime.getRuntime();
        return (instance.totalMemory() - instance.freeMemory()) / 1024;
    }

    public static G fromStream(Stream<E> edgeStream, StreamOrder streamOrder) {
        G graph = new G(streamOrder);
        edgeStream.forEach(edge -> graph.addEdge(edge));
        return graph;
    }
}
