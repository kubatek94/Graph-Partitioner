package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.util.Tuple;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by kubatek94 on 27/04/16.
 */
public class WeightedUnbalancedLdgPartitioner extends GraphPartitioner {
    public WeightedUnbalancedLdgPartitioner(int maxPartitions) {
        super(maxPartitions);
    }

    @Override
    public GraphPartitioner partition(G graph) {
        int numVertices = graph.vertices().size();
        int capacity = Math.round(((float)numVertices/maxPartitions) * 5f); //highly over-provisioned system

        //create partitions required
        numPartitions = maxPartitions;
        for (int i = 0; i < maxPartitions; i++) {
            partitions[i] = new Partition(capacity);
        }

        Supplier<Partition> minUsedPartition = () -> {
            int minIndex = -1;
            int minUse = Integer.MAX_VALUE;

            for (int i = 0; i < maxPartitions; i++) {
                int use = partitions[i].getUse();
                if (use < minUse) {
                    minIndex = i;
                    minUse = use;
                }
            }

            return partitions[minIndex];
        };

        graph.stream().forEach(v -> {
            //neighbourPartitions will be a map of PartitionIndex => Count of V's neighbours in that partition
            Map<Integer, Long> neighbourPartitions =
                    v.neighs() //get v's direct neighbours
                            .stream() //create stream of them
                            .filter(p -> p.partition() != -1) //filter out neighbours that were not partitioned yet
                            .collect(Collectors.groupingBy(V::partition, Collectors.counting())); //count number of neighbours in each partition

            if (neighbourPartitions.size() > 0) {
                Optional<Tuple<Partition, Float>> bestPartition = neighbourPartitions
                        .entrySet().stream()
                        .map(e -> {
                            Partition p = partitions[e.getKey()];
                            float partitionUse = p.getUsePercent();

                            //weight the number of neighbours in that partition, by the space left
                            float score = e.getValue() * (1f - partitionUse);
                            return new Tuple<>(p, score);
                        })
                        .sorted( (a,b) -> b.second.compareTo(a.second) )
                        .findFirst();

                bestPartition.get().first.addVertex(v);
            } else {
                minUsedPartition.get().addVertex(v);
            }
        });

        return this;
    }
}
