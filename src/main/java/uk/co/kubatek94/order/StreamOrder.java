package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;

import java.util.function.Supplier;

/**
 * Created by kubatek94 on 25/04/16.
 */
public abstract class StreamOrder implements Supplier<V> {
    protected G graph = null;
    public abstract void setGraph(G graph);
}

