package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.V;
import java.util.Iterator;

/**
 * Created by kubatek94 on 10/05/16.
 *
 * HdBfsStreamOrder (High Degree Breadth First Search) will pick a vertex with highest degree (number of neighbours) when .get() is called,
 * then it will pick that vertex's direct neighbours on subsequent .get() calls.
 * When all the neighbours have been traversed .get() will reset and the cycle repeats, until all the vertices have been traversed.
 */
public class HdBfsStreamOrder extends HdfStreamOrder {
    private V currentVertex = null;
    private Iterator<V> neighboursIterator = null;

    @Override
    public V get() {
        if (currentVertex == null) {
            currentVertex = nextVertex();
            currentVertex.outOfStream(true);
            neighboursIterator = currentVertex.neighbours().values().iterator();
            return currentVertex;
        } else {
            while (neighboursIterator.hasNext()) {
                V vertex = neighboursIterator.next();
                if (!vertex.isOutOfStream()) {
                    vertices.remove(vertex);
                    vertex.outOfStream(true);
                    return vertex;
                }
            }

            currentVertex = null;
            return get();
        }
    }
}