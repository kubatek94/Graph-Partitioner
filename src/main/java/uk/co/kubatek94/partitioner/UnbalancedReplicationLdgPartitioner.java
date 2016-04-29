package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.partitioner.GraphPartitioner;
import uk.co.kubatek94.partitioner.Partition;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by kubatek94 on 29/04/16.
 * UnbalancedReplicationLdgPartitioner will place the vertices in partition where they have most neighbours.
 * When a partition will become unbalanced above threshold t, the partitioner will replicate highest degree vertices to under-loaded partitions.
 */
public class UnbalancedReplicationLdgPartitioner extends GraphPartitioner {

    public UnbalancedReplicationLdgPartitioner(int maxPartitions) {
        super(maxPartitions);
    }

    @Override
    public GraphPartitioner partition(G graph) {
        return this;
    }
}
