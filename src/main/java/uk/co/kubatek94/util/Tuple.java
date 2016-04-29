package uk.co.kubatek94.util;

/**
 * Created by kubatek94 on 29/04/16.
 */
public class Tuple<A, B> {
    public A first;
    public B second;

    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", first, second);
    }
}