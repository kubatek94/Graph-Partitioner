package uk.co.kubatek94.util;

import uk.co.kubatek94.dataset.Dataset;
import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.order.*;
import uk.co.kubatek94.partitioner.*;

import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 27/04/16.
 */
public class Evaluator {
    private final G graph;

    public Evaluator(G graph) {
        this.graph = graph;
    }

    public void printPartitionStats() {
        Partition[] partitions = this.graph.partitioner().partitions();

        float minUse = Float.MAX_VALUE;
        float maxUse = Float.MIN_VALUE;

        for (Partition p : partitions) {
            if (p != null) {
                //float use = p.getUse();
                float use = p.getFractionUse();
                if (use < minUse) {
                    minUse = use;
                }
                if (use > maxUse) {
                    maxUse = use;
                }
                System.out.println(p);
            }
        }

        System.out.println("Partition imbalance: " + (maxUse - minUse));
    }

    public double partitionImbalance() {
        Partition[] partitions = this.graph.partitioner().partitions();

        double minUse = Float.MAX_VALUE;
        double maxUse = Float.MIN_VALUE;

        for (Partition p : partitions) {
            if (p != null) {
                double use = p.getUse();
                if (use < minUse) {
                    minUse = use;
                }
                if (use > maxUse) {
                    maxUse = use;
                }
            }
        }

        return (maxUse - minUse);
    }

    public double replicationCost() {
        double numberOfReplicatedVertices = graph.vertices().values().parallelStream()
                .filter(v -> v.partitions().size() > 1)
                .count();

        return numberOfReplicatedVertices / graph.vertices().size();
    }

    public double averageEdgeCut() {
        double sum = graph.vertices()
                .values()
                .parallelStream()
                .map(v -> {
                    Set<Integer> partitions = v.partitions();

                    long outPartition = v.neighbours().values()
                            .stream()
                            .filter(n -> Collections.disjoint(partitions, n.partitions())) //filter out neighbours that are on the same partition
                            .count(); //count the neighbours left in the stream

                    long inPartition = v.size() - outPartition;

                    //ratio between neighbours in different partitions and total number of neighbours
                    return ((double) outPartition / (inPartition + outPartition));
                })
                .reduce(0.0, (a, b) -> (a + b));

        return sum / graph.vertices().size();
    }

    public static EvaluationResult evaluatePartitioner(GraphPartitioner partitioner, StreamOrder streamOrder, G graph, String inputSetName) {
        EvaluationResult result = new EvaluationResult();

        //System.out.println("Evaluating " + inputSetName + " with partitioner " + partitioner + " with " + streamOrder);
        Timer.time();

        long startTime = System.currentTimeMillis();

        graph.setStreamOrder(streamOrder);
        graph.partition(partitioner);

        long endTime = System.currentTimeMillis();

        result.timeTaken = String.valueOf( (endTime-startTime)/1000.0 );

        result.partitionerName = partitioner.toString();
        result.datasetName = inputSetName;
        result.streamOrderName = streamOrder.toString();
        result.numberOfVertices = String.valueOf(graph.vertices().size());
        result.numberOfPartitions = String.valueOf(partitioner.numPartitions());

        Evaluator evaluator = new Evaluator(graph);
        result.edgeCut = String.valueOf(evaluator.averageEdgeCut());
        result.replicationCost = String.valueOf(evaluator.replicationCost());
        result.partitionImbalance = String.valueOf(evaluator.partitionImbalance());

        graph.unpartition();

        return result;
    }
}


