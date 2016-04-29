package uk.co.kubatek94.order;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.graph.V;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kubatek94 on 25/04/16.
 *
 * RandomStreamOrder will pick a random vertex from the graph, every time .get() is called.
 */
public class RandomStreamOrder extends StreamOrder {
    protected Random random;
    protected ArrayList<V> vertices;

    @Override
    public void setGraph(G graph) {
        this.graph = graph;
        random = new Random();
        vertices = new ArrayList<>(graph.vertices().values());
    }

    public V randomVertex() {
        int index = random.nextInt(vertices.size());
        return vertices.remove(index);
    }

    @Override
    public V get() {
        return randomVertex();
    }
}
