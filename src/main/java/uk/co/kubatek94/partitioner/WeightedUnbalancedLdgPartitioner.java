package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.util.Triplet;
import uk.co.kubatek94.util.Tuple;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
        int fractionPerServer = divideAndCeil(numVertices, maxPartitions);

        //create partitions required
        numPartitions = maxPartitions;
        for (int i = 0; i < maxPartitions; i++) {
            partitions[i] = new Partition(i, capacity, fractionPerServer);
        }

        Supplier<Partition> minUsedPartition = () -> {
            int minIndex = -1;
            int minSize = Integer.MAX_VALUE;

            for (int i = 0; i < maxPartitions; i++) {
                int size = partitions[i].getSize();
                if (size < minSize) {
                    minIndex = i;
                    minSize = size;
                }
            }

            return partitions[minIndex];
        };

        graph.stream().forEach(v -> {
            //neighbourPartitions will be a map of PartitionIndex => Count of V's neighbours in that partition
            Map<Integer, Long> neighbourPartitions =
                    v.neighbours().values() //get v's direct neighbours
                            .stream() //create stream of them
                            .filter(n -> !n.partitions().isEmpty()) //filter out neighbours that were not partitioned yet
                            .flatMap(n -> n.partitions().stream())
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Optional<Triplet<Partition, Float, Long>> bestPartition = neighbourPartitions
                    .entrySet().stream()
                    .map(e -> {
                        Partition p = partitions[e.getKey()];
                        float partitionUse = p.getUse();

                        float score = e.getValue() * (1f - partitionUse);
                        return new Triplet<>(p, score, e.getValue());
                    })
                    .sorted( (a,b) -> b.second.compareTo(a.second) )
                    .findFirst();

            //either get best partition, or the least used if vertex has no neighbours in partitions yet
            Triplet<Partition, Float, Long> targetPartition = bestPartition.orElseGet(() -> new Triplet<>(minUsedPartition.get(), 0f, 0L));

            if (targetPartition.third < 3) {
                minUsedPartition.get().addVertex(v);
            } else {
                targetPartition.first.addVertex(v);
            }
        });

        return this;
    }
}
