package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 30/04/16.
 */
public class BufferingLdgPartitioner extends GraphPartitioner {

    public BufferingLdgPartitioner(int maxPartitions) {
        super(maxPartitions);
    }

    @Override
    public GraphPartitioner partition(G graph) {

        Iterator<V> vertices = graph.stream().iterator();

        V next = null;
        V prev = null;

        if (vertices.hasNext()) prev = vertices.next();
        if (vertices.hasNext()) next = vertices.next();

        return this;
    }
}
