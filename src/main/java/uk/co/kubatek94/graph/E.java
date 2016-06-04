package uk.co.kubatek94.graph;

/**
 * Created by kubatek94 on 25/04/16.
 * E represents an edge between two vertices.
 */
public class E {
	/**
	 * first is the id of first vertex
	 */
    public final String first;

	/**
	 * second is the id of second vertex
	 */
    public final String second;

    public E(String first, String second) {
        this.first = first;
        this.second = second;
    }
}
