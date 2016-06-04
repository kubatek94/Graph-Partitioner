package uk.co.kubatek94.graph;

import uk.co.kubatek94.dataset.Dataset;
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
    public enum Type {
	    DIRECTED, UNDIRECTED
    };

	private final Type type;
    private ConcurrentHashMap<String, V> vertices = null;
    private StreamOrder streamOrder = null;
    private GraphPartitioner graphPartitioner = null;

    public G(Type type) {
        this(type, null);
    }

    public G(Type type, StreamOrder streamOrder) {
	    this.type = type;
        this.streamOrder = streamOrder;

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

	private G addEdgeDirected(E edge) {
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

		//connect first with second, as its a directed graph
		first.addNeighbour(second);
		return this;
	}

	private G addEdgeUndirected(E edge) {
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

		//connect two vertices, as its an undirected graph
		first.addNeighbour(second);
		second.addNeighbour(first);

		return this;
	}

    public G addEdge(E edge) {
	    if (type == Type.DIRECTED) {
		    addEdgeDirected(edge);
	    } else {
		    addEdgeUndirected(edge);
	    }

        return this;
    }

    public G setStreamOrder(StreamOrder streamOrder) {
        this.streamOrder = streamOrder;
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

    /**
     * This method resets partition indices for all vertices, so we can re-partition the graph,
     * without reading it from input source again.
     * @return
     */
    public G unpartition() {
        vertices.values().parallelStream().forEach(v -> {
            v.partitions().clear();
            v.outOfStream(false);
        });
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

	public static G fromDataset(Dataset dataset, StreamOrder streamOrder) {
		G graph = new G(dataset.getGraphType(), streamOrder);

		//we know the type of graph so lets use specific addEdge call to optimise a little
		if (dataset.getGraphType() == Type.DIRECTED) {
			dataset.getEdgeStream().forEach(graph::addEdgeDirected);
		} else {
			dataset.getEdgeStream().forEach(graph::addEdgeUndirected);
		}

		return graph;
	}

	public static G fromDataset(Dataset dataset) {
		return fromDataset(dataset, null);
	}
}
