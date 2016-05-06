package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.util.array.SortedArray;

import java.util.Iterator;

/**
 * Created by kubatek94 on 28/04/16.
 *
 * HdfStreamOrder (High Degree First) will pick a vertex with highest degree (number of neighbours) when .get() is called,
 * then it will pick that vertex's direct neighbours on subsequent .get() calls.
 * When all the neighbours have been traversed .get() will reset and the cycle repeats, until all the vertices have been traversed.
 */
public class HdfStreamOrder extends StreamOrder {
    protected SortedArray<V> vertices;
    private V currentVertex = null;
    private Iterator<V> neighboursIterator = null;

    @Override
    public void setGraph(G graph) {
        this.graph = graph;

        vertices = new SortedArray<V>(graph.vertices().values(), (a, b) -> {
            //sort by the degree first, then by the id if they have equal size
            int result = b.size().compareTo(a.size());
            if (result == 0) {
                return b.id().compareTo(a.id());
            }
            return result;
        });
    }

    protected V nextVertex() {
        return vertices.removeFirst();
    }

    @Override
    public V get() {
        //return nextVertex();
        if (currentVertex == null) {
            currentVertex = nextVertex();
            neighboursIterator = currentVertex.neighbours().values().iterator();
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
    }
}