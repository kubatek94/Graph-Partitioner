package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.util.array.SortedArray;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by kubatek94 on 25/04/16.
 *
 * BfsStreamOrder (Breadth First Search) will pick a random vertex when .get() is called, then it will pick that vertex's direct neighbours on subsequent .get() calls.
 * When all the neighbours have been traversed .get() will reset and cycle will repeat, until all vertices have been traversed.
 */
public class BfsStreamOrder extends StreamOrder {
    private SortedArray<V> vertices;
    private V currentVertex = null;
    private Iterator<V> neighboursIterator = null;
    private Random random;

    public BfsStreamOrder(G graph) {
        super(graph);
        random = new Random();
        vertices = new SortedArray<>(graph.vertices().values(), (a, b) -> b.id().compareTo(a.id()) );
    }

    public V randomVertex() {
        int index = random.nextInt(vertices.size());
        return vertices.remove(index);
    }

    @Override
    public V get() {
        if (currentVertex == null) {
            currentVertex = randomVertex();
            neighboursIterator = currentVertex.neighs().iterator();
            return currentVertex;
        } else {
            while (neighboursIterator.hasNext()) {
                V vertex = neighboursIterator.next();
                if (vertex.partitions().isEmpty()) {
                    vertices.remove(vertex);
                    return vertex;
                }
            }

            currentVertex = null;
            return get();
        }


        /*if (currentVertex == null) {
            currentVertex = randomVertex();
            //neighs = currentVertex.neighs();
            return currentVertex;
        } else {
            V vertex = neighs.first();
            if (vertex.partition() == -1) {
                vertices.remove(vertex);
                return vertex;
            }

            currentVertex = null;
            return get();
        }*/
    }
}
