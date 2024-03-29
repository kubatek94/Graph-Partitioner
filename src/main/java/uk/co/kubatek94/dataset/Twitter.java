package uk.co.kubatek94.dataset;

import uk.co.kubatek94.graph.E;
import uk.co.kubatek94.graph.G;

import java.util.stream.Stream;

/**
 * Created by kubatek94 on 10/05/16.
 */
public class Twitter extends Dataset {
    private final static String path = "/home/kubatek94/Desktop/DissertationProject/datasets/twitter/twitter_combined.csv";

    public Twitter() {
        super(path, G.Type.DIRECTED);
    }

    @Override
    public Stream<E> getEdgeStream() {
        return stream
                .parallel()
                .map(s -> s.split(" ")) //split the line, so that it becomes an edge between two node ids
                .map(e -> new E(e[0], e[1]));
    }
}
