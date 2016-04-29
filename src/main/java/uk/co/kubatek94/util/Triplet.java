package uk.co.kubatek94.util;

/**
 * Created by kubatek94 on 29/04/16.
 */
public class Triplet<A,B,C> {
    public A first;
    public B second;
    public C third;

    public Triplet(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s)", first, second, third);
    }
}
