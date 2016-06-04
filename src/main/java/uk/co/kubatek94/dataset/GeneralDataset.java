package uk.co.kubatek94.dataset;

import uk.co.kubatek94.graph.E;
import uk.co.kubatek94.graph.G;

import java.util.stream.Stream;

/**
 * Created by kubatek94 on 11/05/16.
 */
public class GeneralDataset extends Dataset {
    private String name = "GeneralDataset";

    public GeneralDataset(String name, String path, G.Type graphType) {
        super(path, graphType);
        this.name = name;
    }

    @Override
    public Stream<E> getEdgeStream() {
        return stream
                .parallel()
                .map(s -> s.split(" ")) //split the line, so that it becomes an edge between two node ids
                .map(e -> new E(e[0], e[1]));
    }

    @Override
    public String toString(){
        return name;
    }
}
