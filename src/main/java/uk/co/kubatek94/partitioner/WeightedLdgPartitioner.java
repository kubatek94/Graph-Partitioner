package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by kubatek94 on 27/04/16.
 */
public class WeightedLdgPartitioner extends GraphPartitioner {
    public WeightedLdgPartitioner(int maxPartitions) {
        super(maxPartitions);
    }

    @Override
    public GraphPartitioner partition(G graph) {
        int numVertices = graph.vertices().size();
        int capacity = Math.round(((float)numVertices/maxPartitions) * 1.1f); //add more space to make sure that all vertices will fit

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

            int neighbourPartitionsSize = neighbourPartitions.size();
            Partition minUsed = minUsedPartition.get();

            if (neighbourPartitionsSize > 0) {
                List<Tuple<Integer, Float>> partitionScores = neighbourPartitions
                        .entrySet().stream()
                        .map(e -> {
                            int partitionIndex = e.getKey(); //take the partition index
                            float partitionUse = partitions[partitionIndex].getUsePercent();

                            //weight the number of neighbours in that partition, by the space left
                            float score = e.getValue() * (1f - partitionUse);
                            return new Tuple<>(partitionIndex, score);
                        })
                        .sorted( (a,b) -> b.second.compareTo(a.second) )
                        .collect(Collectors.toList());

                for (int i = 0; i < neighbourPartitionsSize; i++) {
                    Tuple<Integer, Float> partitionScore = partitionScores.get(i);
                    Partition bestPartition = partitions[partitionScore.first];

                    if (bestPartition.getUsePercent() - minUsed.getUsePercent() < 0.05) {
                        bestPartition.addVertex(v);
                        break;
                    }
                }

                //couldn't add to any partition where there are neighbours already
                //so add vertex to minUsed partition
                if (v.partition() == -1) {
                    minUsed.addVertex(v);
                }
            } else {
                minUsed.addVertex(v);
            }
        });

        return this;
    }

    private class Tuple<A, B> {
        public A first;
        public B second;

        public Tuple(A first, B second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return String.format("(%s,%s)", first.toString(), second.toString());
        }
    }
}
