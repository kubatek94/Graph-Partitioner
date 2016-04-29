package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;

/**
 * Created by kubatek94 on 25/04/16.
 */
public class HashPartitioner extends GraphPartitioner {
    private int numVertices = 0;

    public HashPartitioner(int maxPartitions) {
        super(maxPartitions);
    }

    @Override
    public GraphPartitioner partition(G graph) {
        numVertices = graph.vertices().size();
        int capacity = Math.round(((float)numVertices/maxPartitions) * 1.5f); //highly over-provisioned system
        int fractionPerServer = divideAndCeil(numVertices, maxPartitions);

        //create partitions required
        numPartitions = maxPartitions;
        for (int i = 0; i < maxPartitions; i++) {
            partitions[i] = new Partition(capacity, fractionPerServer); //add more space to make sure that all vertices will fit
        }

        //loop through all vertices and assign each to partition
        graph.stream().forEach(v -> {
            int part = indexForHash(v.hashCode(), numPartitions);
            partitions[part].addVertex(v);
        });
        return this;
    }
}
