package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.V;

/**
 * Created by kubatek94 on 10/05/16.
 *
 * LdBfsStreamOrder (Low Degree Breadth First Search) will pick a vertex with lowest degree (number of neighbours) when .get() is called,
 * then it will pick that vertex's direct neighbours on subsequent .get() calls.
 * When all the neighbours have been traversed .get() will reset and the cycle repeats, until all the vertices have been traversed.
 */
public class LdBfsStreamOrder extends HdBfsStreamOrder {
    @Override
    protected V nextVertex() {
        return vertices.removeLast();
    }
}
