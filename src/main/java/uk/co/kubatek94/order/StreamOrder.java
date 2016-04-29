package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;

import java.util.function.Supplier;

/**
 * Created by kubatek94 on 25/04/16.
 */
public abstract class StreamOrder implements Supplier<V> {
    protected final G graph;

    public StreamOrder(G graph) {
        this.graph = graph;
    }
}

