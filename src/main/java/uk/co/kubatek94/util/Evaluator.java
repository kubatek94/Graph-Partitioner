package uk.co.kubatek94.util;

import uk.co.kubatek94.graph.G;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kubatek94 on 27/04/16.
 */
public class Evaluator {
    private final G graph;

    public Evaluator(G graph) {
        this.graph = graph;
    }

    public float averageEdgeCut() {
        final FloatWrap totalEdgesCut = new FloatWrap(0);

        graph.vertices().values().stream().forEach(v -> {
            Map<Integer, Long> partitionStats = v.neighs() //get v's direct neighbours
                    .stream() //create stream of them
                    .collect(Collectors.groupingBy(p -> p.partition(), Collectors.counting())); //count number of neighbours in each partition

            long outPartition = partitionStats.entrySet().stream()
                    .filter(e -> e.getKey() != v.partition()) //leave only neighbours that are on different partition
                    .collect(Collectors.summingLong(e -> e.getValue())); //sum all the neighbours

            long inPartition = partitionStats.entrySet().stream()
                    .filter(e -> e.getKey() == v.partition()) //leave only neighbours that are on the same partition
                    .collect(Collectors.summingLong(e -> e.getValue())); //sum all the neighbours

            //ratio between neighbours in different partitions and total number of neighbours
            float edgesCut = ((float) outPartition / (inPartition + outPartition));

            totalEdgesCut.value = totalEdgesCut.value + edgesCut;
            //System.out.println(v + " | +" + inPartition + " | -" + outPartition + " ___ " + partitionStats);
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
