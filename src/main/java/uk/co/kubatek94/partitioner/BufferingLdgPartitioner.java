package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;
import uk.co.kubatek94.util.Evaluator;
import uk.co.kubatek94.util.Tuple;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 30/04/16.
 */
public class BufferingLdgPartitioner extends GraphPartitioner {
    public BufferingLdgPartitioner(int maxPartitions) {
        super(maxPartitions);
    }

    @Override
    public GraphPartitioner partition(G graph) {
        overProvision = 3f;
        super.partition(graph);

        Iterator<V> vertices = graph.stream().iterator();
        LinkedList<V> group = new LinkedList<>();
	    V head = null;

        if (vertices.hasNext()) head = vertices.next();

        while (vertices.hasNext()) {
            group.addFirst(head);

            //take next vertex
            V curr = vertices.next();

            //place it at the back of the list as long as it is head's neighbour
            while (head.hasNeighbour(curr)) {
                group.addLast(curr);

                if (vertices.hasNext()) {
                    curr = vertices.next();
                } else {
                    break;
                }
            }

            //if element wasn't placed in the list, then it becomes the new head
            if (curr != group.getLast()) {
                head = curr;
            }

            //take that list of vertices and place them in a partition
            partitionGroup(group);

            //clear the group
            group.clear();
        }

        return this;
    }

    private void partitionGroup(List<V> vertices) {
        //get least used partition
        Partition selectedPartition = getMinMaxPartitions().first;

        //place all vertices in there
        vertices.forEach(v -> selectedPartition.addVertex(v));
    }
}
