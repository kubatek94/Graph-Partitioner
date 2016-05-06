package uk.co.kubatek94;

import uk.co.kubatek94.dataset.Dataset;
import uk.co.kubatek94.dataset.Facebook;
import uk.co.kubatek94.dataset.Gplus;
import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.order.BfsStreamOrder;
import uk.co.kubatek94.order.HdfStreamOrder;
import uk.co.kubatek94.order.LdfStreamOrder;
import uk.co.kubatek94.order.RandomStreamOrder;
import uk.co.kubatek94.partitioner.*;
import uk.co.kubatek94.util.Evaluator;
import uk.co.kubatek94.util.Timer;

public class Main {

    public static void main(String[] args) {
        Dataset inputSet = new Facebook();

        Timer.time();
        G graph = G.fromStream(inputSet.getEdgeStream(), new BfsStreamOrder());
        System.out.println("Took: " + (Timer.time()/1000.0) + " seconds to load graph into memory");
        System.out.println("Number of vertices: " + graph.vertices().size());

        Timer.time();
        //graph.partition(new WeightedLdgPartitioner(4));
        //graph.partition(new ReplicationLdgPartitioner(4));
        graph.partition(new BufferingLdgPartitioner(4));
        //graph.partition(new WeightedUnbalancedLdgPartitioner(4));
        System.out.println("Took: " + (Timer.time()/1000.0) + " seconds to partition");

        Evaluator evaluator = new Evaluator(graph);

        System.out.println("Number of unassigned vertices: " + evaluator.unassignedVertices());
        System.out.println("Average edge cut: " + evaluator.averageEdgeCut());
        System.out.println("Replication cost: " + evaluator.replicationCost());

        evaluator.printPartitionStats();
    }
}
