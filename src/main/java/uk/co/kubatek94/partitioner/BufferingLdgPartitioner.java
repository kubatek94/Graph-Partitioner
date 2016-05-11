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
        int numVertices = graph.vertices().size();
        int capacity = Math.round(((float)numVertices/maxPartitions) * 3f); //highly over-provisioned system
        int fractionPerServer = divideAndCeil(numVertices, maxPartitions);

        //create partitions required
        numPartitions = maxPartitions;
        for (int i = 0; i < maxPartitions; i++) {
            partitions[i] = new Partition(i, capacity, fractionPerServer); //add more space to make sure that all vertices will fit
        }

        Iterator<V> vertices = graph.stream().iterator();

        V head = null;
        LinkedList<V> group = new LinkedList<>();

        if (vertices.hasNext()) head = vertices.next();

        while (head != null) {
            group.addFirst(head);

            //if there are more vertices
            if (vertices.hasNext()) {
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
            } else {
                head = null;
            }

            //take that list of vertices and place them in a partition
            partitionGroup(group);

            //clear the group
            group.clear();
        }

        return this;
    }

    private Tuple<Partition,Partition> getMinMaxPartitions() {
        int minIndex = -1;
        int minSize = Integer.MAX_VALUE;

        int maxIndex = -1;
        int maxSize = Integer.MIN_VALUE;

        for (int i = 0; i < maxPartitions; i++) {
            int size = partitions[i].getSize();
            if (size < minSize) {
                minIndex = i;
                minSize = size;
            }
            if (size > maxSize) {
                maxSize = size;
                maxIndex = i;
            }
        }

        return new Tuple<>(partitions[minIndex], partitions[maxIndex]);
    }

    private void partitionGroup(List<V> vertices) {
        //get least used partition
        Partition selectedPartition = getMinMaxPartitions().first;

        //place all vertices in there
        vertices.forEach(v -> selectedPartition.addVertex(v));
    }
}
