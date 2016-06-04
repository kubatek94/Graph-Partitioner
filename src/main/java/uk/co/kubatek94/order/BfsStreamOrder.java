package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.util.array.SortedArray;

import java.util.*;

/**
 * Created by kubatek94 on 25/04/16.
 *
 * BfsStreamOrder (Breadth First Search) will pick a random vertex when .get() is called, then it will perform a breadth first search on its neighbours.
 */
public class BfsStreamOrder extends StreamOrder {
    private LinkedList<V> queue = null;

    public void setGraph(G graph) {
        this.graph = graph;
        queue = new LinkedList<>();

        //pick a random vertex
        ArrayList<V> vertices = new ArrayList<>(graph.vertices().values());
        int index = new Random().nextInt(vertices.size());
        V vertex = vertices.get(index);
	    vertices = null;

        queue.addLast(vertex);
    }

	@Override
	public V get() {
		V vertex = queue.removeFirst();

		while (vertex.isOutOfStream()) {
			vertex = queue.removeFirst();
		}

		vertex.outOfStream(true);
		Collection<V> neighbours = vertex.neighbours().values();
		neighbours.forEach(neighbour -> {
			if (!neighbour.isOutOfStream()) {
				queue.addLast(neighbour);
			}
		});

		return vertex;
	}
}
