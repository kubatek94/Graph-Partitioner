package uk.co.kubatek94.partitioner;

import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.util.Tuple;

/**
 * Created by kubatek94 on 25/04/16.
 */
public abstract class GraphPartitioner {
    protected final int numPartitions;
    protected Partition[] partitions;
	protected float overProvision = 5f;

    public GraphPartitioner(int numPartitions) {
	    this.numPartitions = numPartitions;
        this.partitions = new Partition[numPartitions];

	    //create partitions required
	    for (int i = 0; i < numPartitions; i++) {
		    partitions[i] = new Partition(i); //add more space to make sure that all vertices will fit
	    }
    }

    public int numPartitions() {
        return numPartitions;
    }

    public Partition[] partitions() {
        return partitions;
    }

    public GraphPartitioner partition(G graph) {
	    int numVertices = graph.vertices().size();
	    int capacity = Math.round(((float)numVertices/numPartitions) * overProvision); //over-provisioned system
	    int targetFraction = divideAndCeil(numVertices, numPartitions);

	    for (Partition p : partitions) {
		    p.setCapacity(capacity);
		    p.setTargetFraction(targetFraction);
	    }

	    return this;
    }

	/**
	 * Gets a tuple with least used partition in the first item, and most used partition in the second item
	 * @return Tuple
	 */
    public Tuple<Partition,Partition> getMinMaxPartitions() {
        int minIndex = -1;
        int minSize = Integer.MAX_VALUE;

        int maxIndex = -1;
        int maxSize = Integer.MIN_VALUE;

        for (int i = 0; i < numPartitions; i++) {
            int size = partitions[i].getSize();
            if (size < minSize) {
                minIndex = i;
                minSize = size;
            }
            if (size > maxSize) {
                maxSize = size;
                maxIndex = i;
            }
        }

        return new Tuple<>(partitions[minIndex], partitions[maxIndex]);
    }

	/**
	 * Gets least used partition
	 * @return Partition
	 */
	public Partition getMinPartition() {
		int minIndex = -1;
		int minSize = Integer.MAX_VALUE;

		for (int i = 0; i < numPartitions; i++) {
			int size = partitions[i].getSize();
			if (size < minSize) {
				minIndex = i;
				minSize = size;
			}
		}

		return partitions[minIndex];
	}

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

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
