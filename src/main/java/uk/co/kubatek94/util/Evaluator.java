package uk.co.kubatek94.util;

import uk.co.kubatek94.graph.G;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by kubatek94 on 27/04/16.
 */
public class Evaluator {
    private final G graph;

    public Evaluator(G graph) {
        this.graph = graph;
    }

    public float replicationCost() {
        float numberOfReplicatedVertices = graph.vertices().values().stream()
                .filter(v -> v.partitions().size() > 1)
                .count();

        return numberOfReplicatedVertices / graph.vertices().size();
    }

    public float averageEdgeCut() {
        final FloatWrap totalEdgesCut = new FloatWrap(0);

        graph.vertices().values().stream().forEach(v -> {
            //neighbourPartitions will be a map of PartitionIndex => Count of V's neighbours in that partition
            Map<Integer, Long> neighbourPartitions =
                    v.neighs() //get v's direct neighbours
                            .stream() //create stream of them
                            .filter(n -> !n.partitions().isEmpty()) //filter out neighbours that were not partitioned yet
                            .flatMap(n -> n.partitions().stream())
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Set<Integer> partitions = v.partitions();

            // v partitions = [0, 3]
            // n partitions [1, 3]
            // n partitions [0, 1]
            // n partitions [2]

            // stream => [1, 3, 0, 2]

            // outpartition => 2
            // inpartition => 2
            // cost = 2/4 => 1/2

            // outpartition => 1
            // inpartition => 2
            // cost = 1/3

            long outPartition = neighbourPartitions.entrySet().stream()
                    .filter(e -> !partitions.contains(e.getKey())) //leave only neighbours that are on different partition
                    .collect(Collectors.summingLong(e -> e.getValue())); //sum all the neighbours

            long inPartition = neighbourPartitions.entrySet().stream()
                    .filter(e -> partitions.contains(e.getKey())) //leave only neighbours that are on the same partition
                    .collect(Collectors.summingLong(e -> e.getValue())); //sum all the neighbours


            //ratio between neighbours in different partitions and total number of neighbours
            float edgesCut = ((float) outPartition / (inPartition + outPartition));

            totalEdgesCut.value = totalEdgesCut.value + edgesCut;
        });

        return totalEdgesCut.value / graph.vertices().size();
    }

    public class FloatWrap {
        public float value = 0;

        public FloatWrap(float value) {
            this.value = value;
        }
    }
}
