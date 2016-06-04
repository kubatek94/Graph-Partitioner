package uk.co.kubatek94.dataset;

import uk.co.kubatek94.graph.E;
import uk.co.kubatek94.graph.G;

import java.util.stream.Stream;

/**
 * Created by kubatek94 on 25/04/16.
 */
public class Facebook extends Dataset {
    private final static String path = "/home/kubatek94/Desktop/DissertationProject/datasets/facebook/facebook_combined.csv";

    public Facebook() {
        super(path, G.Type.UNDIRECTED);
    }

    @Override
    public Stream<E> getEdgeStream() {
        return stream
            .parallel()
            .skip(1) //skip the header
            .map(s -> s.split(" ")) //split the line, so that it becomes an edge between two node ids
            .map(e -> new E(e[0], e[1]));
    }
}
