package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.util.array.SortedArray;
import uk.co.kubatek94.util.Tuple;
import uk.co.kubatek94.util.array.SortedArrayIterator;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by kubatek94 on 29/04/16.
 * ReplicatingLdgPartitioner will place the vertices in partition where they have most neighbours.
 * When a partition will become unbalanced above threshold t, the partitioner will replicate highest degree vertices to under-loaded partitions.
 */
public class ReplicatingLdgPartitioner extends GraphPartitioner {

    public ReplicatingLdgPartitioner(int maxPartitions) {
        super(maxPartitions);
    }

    @Override
    public GraphPartitioner partition(G graph) {
        overProvision = 2f;
        super.partition(graph);

        graph.stream().forEach(v -> {
            //find least and most used partitions
            Tuple<Partition,Partition> minMax = getMinMaxPartitions();

            //check if the most used partition exceeds the threshold
            //if it does, then replicate a vertex with highest degree
            //from the most used partition and put it in the least used partition
            float threshold = minMax.second.getUse() - minMax.first.getUse();
            if (threshold > 0.05) {
                //replicate highest degree vertex to one partition at a time
                //if it was replicated to all the partitions,
                //then replicate the next highest degree vertex
                Iterator<V> iterator = ((SortedPartition) minMax.second).iterator();
                while (iterator.hasNext()) {
                    V highDegreeVertex = iterator.next();
                    Set<Integer> partitions = highDegreeVertex.partitions();

                    if (!partitions.contains(minMax.first.id())){
                        minMax.first.addVertex(highDegreeVertex);
                        //System.out.format("Replicate %s to partition %d\n", highDegreeVertex, minMax.first.id());
                        //System.out.format("Replicated %s, Partitions: %s\n", highDegreeVertex, highDegreeVertex.partitions());
                        break;
                    }
                }
            }

            //neighbourPartitions will be a map of PartitionIndex => Count of V's neighbours in that partition
            Map<Integer, Long> neighbourPartitions =
                    v.neighbours().values() //get v's direct neighbours
                            .stream() //create stream of them
                            .filter(n -> !n.partitions().isEmpty()) //filter out neighbours that were not partitioned yet
                            .flatMap(n -> n.partitions().stream())
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


            if (neighbourPartitions.size() > 0) {
                Optional<Tuple<Partition, Float>> bestPartition = neighbourPartitions
                        .entrySet().stream()
                        .map(e -> {
                            Partition p = partitions[e.getKey()];
                            float partitionUse = p.getUse();

                            //weight the number of neighbours in that partition, by the space left
                            float score = e.getValue() * (1f - partitionUse);
                            return new Tuple<>(p, score);
                        })
                        .sorted( (a,b) -> b.second.compareTo(a.second) )
                        .findFirst();

                bestPartition.get().first.addVertex(v);
            } else {
                //add to least used partition
                minMax.first.addVertex(v);
            }
        });

        return this;
    }
}
