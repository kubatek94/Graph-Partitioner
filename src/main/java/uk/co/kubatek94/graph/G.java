package uk.co.kubatek94.graph;

import uk.co.kubatek94.order.RandomStreamOrder;
import uk.co.kubatek94.order.StreamOrder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 25/04/16.
 */
public class G {
    private ConcurrentHashMap<String, V> vertices = null;
    private StreamOrder streamOrder = null;

    public G() {
        vertices = new ConcurrentHashMap<>();
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
        }/* else {
            first.init();
        }*/

        if (previousSecond != null) {
            second = previousSecond;
        }/* else {
            second.init();
        }*/

        //connect two vertices
        first.addNeighbour(second);
        second.addNeighbour(first);

        return this;
    }

    public G setStreamOrder(StreamOrder streamOrder) {
        this.streamOrder = streamOrder;
        return this;
    }

    public Stream<V> stream() {
        if (this.streamOrder == null) {
            this.streamOrder = new RandomStreamOrder(this);
        }

        //this.streamOrder.setGraph(this);
        return Stream.generate(this.streamOrder).sequential().limit(vertices.size());
    }

    public long usedMemory() {
        Runtime instance = Runtime.getRuntime();
        return (instance.totalMemory() - instance.freeMemory()) / 1024;
    }

    public static G fromStream(Stream<E> edgeStream) {
        G graph = new G();

        edgeStream.forEach(edge -> graph.addEdge(edge));

        return graph;
    }
}
