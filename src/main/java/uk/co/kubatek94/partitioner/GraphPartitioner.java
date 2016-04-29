package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;

/**
 * Created by kubatek94 on 25/04/16.
 */
public abstract class GraphPartitioner {
    protected final int maxPartitions;
    protected int numPartitions = 1;
    protected final Partition[] partitions;

    public GraphPartitioner(int maxPartitions) {
        this.maxPartitions = maxPartitions;
        this.partitions = new Partition[maxPartitions];
    }

    public Partition[] partitions() {
        return partitions;
    }

    public abstract GraphPartitioner partition(G graph);

    /**
     * Applies a supplemental hash function to a given hashCode, which
     * defends against poor quality hash functions.  This is critical
     * because HashMap uses power-of-two length hash tables, that
     * otherwise encounter collisions for hashCodes that do not differ
     * in lower bits.
     *
     * Then returns an index. tableLength must be a power of 2.
     */
    public static int indexForHash(int h, int tableLength) {
        //rehash first
        h ^= (h >>> 20) ^ (h >>> 12);
        h =  h ^ (h >>> 7) ^ (h >>> 4);

        return h & (tableLength-1);
    }

    /**
     * http://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil
     * @return ceil(numerator/denominator)
     */
    public static int divideAndCeil(int numerator, int denominator) {
        return (numerator - 1) / denominator + 1;
    }
}
