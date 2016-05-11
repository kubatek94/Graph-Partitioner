package uk.co.kubatek94.dataset;

import uk.co.kubatek94.graph.E;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 25/04/16.
 */
public abstract class Dataset {
    protected Stream<String> stream = null;

    public Dataset(String path) {
        try {
            stream = Files.lines(Paths.get(path));
        } catch (IOException e) {
            throw new InvalidPathException(path, "Could not open file!");
        }
    }

    public abstract Stream<E> getEdgeStream();

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
