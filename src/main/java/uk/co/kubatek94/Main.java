package uk.co.kubatek94;

import uk.co.kubatek94.dataset.*;
import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.order.*;
import uk.co.kubatek94.partitioner.*;
import uk.co.kubatek94.util.Evaluator;
import uk.co.kubatek94.util.Timer;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws InterruptedException, IOException {
		//Dataset inputSet = new Twitter();
        Dataset inputSet = new Facebook();
		//Dataset inputSet = new Gplus();

        Timer.time();
        G graph = G.fromStream(inputSet.getEdgeStream(), new RandomFirstOrderStreamOrder());
        System.out.println("Took: " + (Timer.time()/1000.0) + " seconds to load graph into memory");
        System.out.println("Number of vertices: " + graph.vertices().size());

        Timer.time();
        graph.partition(new WeightedLdgPartitioner(4));
        //graph.partition(new ReplicatingLdgPartitioner(4));
        //graph.partition(new BufferingLdgPartitioner(4));
        //graph.partition(new WeightedUnbalancedLdgPartitioner(4));
        System.out.println("Took: " + (Timer.time()/1000.0) + " seconds to partition");

        Evaluator evaluator = new Evaluator(graph);

        Timer.time();
        System.out.println("Average edge cut: " + evaluator.averageEdgeCut());
        System.out.println("Took: " + (Timer.time()/1000.0) + " seconds to calculate average edge cut");

        Timer.time();
        System.out.println("Replication cost: " + evaluator.replicationCost());
        System.out.println("Took: " + (Timer.time()/1000.0) + " seconds to calculate replication cost");

        evaluator.printPartitionStats();
	}
}
