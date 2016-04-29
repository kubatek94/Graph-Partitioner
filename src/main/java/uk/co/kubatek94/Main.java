package uk.co.kubatek94;

import uk.co.kubatek94.dataset.Dataset;
import uk.co.kubatek94.dataset.Facebook;
import uk.co.kubatek94.dataset.Gplus;
import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.order.HdfStreamOrder;
import uk.co.kubatek94.order.LdfStreamOrder;
import uk.co.kubatek94.partitioner.*;
import uk.co.kubatek94.util.Evaluator;
import uk.co.kubatek94.util.Timer;

public class Main {

    public static void main(String[] args) {
        Dataset inputSet = new Gplus();

        Timer.time();
        G graph = G.fromStream(inputSet.getEdgeStream());
        System.out.println("Took: " + (Timer.time()/1000.0) + " seconds to load graph into memory");
        System.out.println("Number of vertices: " + graph.vertices().size());

        Timer.time();

        //GraphPartitioner partitioner = new HashPartitioner(4);
        //GraphPartitioner partitioner = new WeightedLdgPartitioner(4);
        GraphPartitioner partitioner = new WeightedUnbalancedLdgPartitioner(4);
        partitioner.partition(graph.setStreamOrder(new LdfStreamOrder(graph)));
        //partitioner.partition(graph.setStreamOrder(new BfsStreamOrder(graph)));
        //partitioner.partition(graph.setStreamOrder(new RandomStreamOrder(graph)));

        //graph.setStreamOrder(new HdfStreamOrder(graph));
        //graph.stream().count();//.forEach(System.out::println);

        System.out.println("Took: " + (Timer.time()/1000.0) + " seconds to partition");

        for (Partition p : partitioner.partitions()) {
            System.out.println(p);
        }

        //for(V v : graph.vertices().values()) {
        //    System.out.println(v);
        //}

        System.out.println("Unassigned vertices number: " + graph.vertices().values().stream().filter(v -> v.partition() < 0).count());

        /*graph.vertices().values().stream().forEach(v -> {
           System.out.println(v + " => " + v.neighs());
        });*/

        Evaluator evaluator = new Evaluator(graph);

        System.out.println("Average edge cut: " + evaluator.averageEdgeCut());
    }
}
