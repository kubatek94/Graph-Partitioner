package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;

/**
 * Created by kubatek94 on 25/04/16.
 */
public class HashPartitioner extends GraphPartitioner {
    public HashPartitioner(int numPartitions) {
        super(numPartitions);
    }

    @Override
    public GraphPartitioner partition(G graph) {
	    overProvision = 1.5f;
	    super.partition(graph);

        //loop through all vertices and assign each to partition
        graph.stream().forEach(v -> {
            int ind = indexForHash(v.hashCode(), numPartitions);
            partitions[ind].addVertex(v);
        });

        return this;
    }
}
