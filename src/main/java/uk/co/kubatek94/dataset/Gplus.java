package uk.co.kubatek94.dataset;

import uk.co.kubatek94.graph.E;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 25/04/16.
 */
public class Gplus extends Dataset {
    private final static String path = "/home/kubatek94/Desktop/DissertationProject/datasets/gplus/gplus_combined.csv";

    public Gplus() {
        super(path);
    }

    @Override
    public Stream<E> getEdgeStream() {
        return stream
                .parallel()
                .map(s -> s.split(" ")) //split the line, so that it becomes an edge between two node ids
                .map(e -> new E(e[0], e[1]));
    }
}
