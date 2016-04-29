package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;

/**
 * Created by kubatek94 on 28/04/16.
 */
public class LdfStreamOrder extends HdfStreamOrder {

    public LdfStreamOrder(G graph) {
        super(graph);
    }

    @Override
    protected V nextVertex() {
        return vertices.removeLast();
    }
}
