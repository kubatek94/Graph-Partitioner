package uk.co.kubatek94.util;

import uk.co.kubatek94.graph.G;

import java.util.Collections;
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
            Set<Integer> partitions = v.partitions();

            long outPartition = v.neighs()
                    .stream()
                    .filter(n -> Collections.disjoint(partitions, n.partitions())) //filter out neighbours that are on the same partition
                    .count(); //count the neighbours left in the stream

            long inPartition = v.size() - outPartition;

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
