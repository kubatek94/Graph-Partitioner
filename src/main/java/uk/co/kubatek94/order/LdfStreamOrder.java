package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;

/**
 * Created by kubatek94 on 28/04/16.
 * LdfStreamOrder (Low Degree First) will pick next vertex in graph with lowest degree (number of neighbours) every time .get() is called.
 */
public class LdfStreamOrder extends HdfStreamOrder {
    @Override
    protected V nextVertex() {
        return vertices.removeLast();
    }
}
