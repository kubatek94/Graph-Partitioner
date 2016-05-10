package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.util.array.SortedArray;

import java.util.Iterator;

/**
 * Created by kubatek94 on 28/04/16.
 * HdfStreamOrder (High Degree First) will pick next vertex in graph with highest degree (number of neighbours) every time .get() is called.
 */
public class HdfStreamOrder extends StreamOrder {
    protected SortedArray<V> vertices;

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
        return nextVertex();
    }
}