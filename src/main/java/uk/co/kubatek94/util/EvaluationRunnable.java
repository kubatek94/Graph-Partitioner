package uk.co.kubatek94.util;

import uk.co.kubatek94.dataset.Dataset;
import uk.co.kubatek94.graph.G;
import uk.co.kubatek94.order.*;
import uk.co.kubatek94.partitioner.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 11/05/16.
 */
public class EvaluationRunnable implements Runnable {
    private int numberOfPartitions;
    private int numberOfRepeats;
    private Stream<Dataset> queue;
    private final RandomIdentifierGenerator identifierGenerator;
    private final ConcurrentHashMap<String, BufferedWriter> writers;

    public EvaluationRunnable(Stream<Dataset> queue, int numberOfPartitions, int numberOfRepeats) {
        this.queue = queue;
        this.numberOfPartitions = numberOfPartitions;
        this.numberOfRepeats = numberOfRepeats;

        this.identifierGenerator = new RandomIdentifierGenerator();
        this.writers = new ConcurrentHashMap<>();
    }

    public final class RandomIdentifierGenerator {
        private SecureRandom random = new SecureRandom();
        public String nextRandomIdentifier() {
            return new BigInteger(130, random).toString(32);
        }
    }

    @Override
    public void run() {
        //Load graphs in sequence, as single graph load is parallelised anyway
        List<Tuple<String, G>> graphs = queue.sequential().map(dataset -> {
            System.out.print("Loading graph... ");
            G graph = G.fromStream(dataset.getEdgeStream());
            System.out.println("done.");

            return new Tuple<>(dataset.toString(), graph);
        }).collect(Collectors.toList());

        graphs.stream().flatMap(tuple -> {
            String fileName = tuple.first + "_" + identifierGenerator.nextRandomIdentifier() + ".csv";
            EvaluationResultSupplier resultSupplier = new EvaluationResultSupplier(tuple.second, tuple.first, fileName, numberOfRepeats, numberOfPartitions);
            writers.put(fileName, resultSupplier.writer);

            return Stream.generate(resultSupplier).limit(resultSupplier.getLength()).parallel();
        }).parallel().forEach(evaluationResult -> {
            BufferedWriter writer = evaluationResult.first;
            EvaluationResult result = evaluationResult.second;

            try {
                writer.write(result.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writers.values().forEach(writer -> {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static class GraphPartitionerSupplier implements Supplier<GraphPartitioner> {
        private List<Supplier<GraphPartitioner>> partitionerSuppliers = null;
        private Iterator<Supplier<GraphPartitioner>> partitionerSupplierIterator = null;

        private int numberOfRepeats = 1;
        private int served = 0;

        private Supplier<GraphPartitioner> currentGraphPartitionerSupplier = null;

        public GraphPartitionerSupplier(int numberOfPartitions, int numberOfRepeats) {
            this.numberOfRepeats = numberOfRepeats;

            partitionerSuppliers = new LinkedList<>();
            partitionerSuppliers.add(() -> new HashPartitioner(numberOfPartitions));
            partitionerSuppliers.add(() -> new WeightedLdgPartitioner(numberOfPartitions));
            partitionerSuppliers.add(() -> new WeightedUnbalancedLdgPartitioner(numberOfPartitions));
            partitionerSuppliers.add(() -> new BufferingLdgPartitioner(numberOfPartitions));
            partitionerSuppliers.add(() -> new ReplicatingLdgPartitioner(numberOfPartitions));

            partitionerSupplierIterator = partitionerSuppliers.iterator();
        }

        public int getLength() {
            return partitionerSuppliers.size() * numberOfRepeats;
        }

        @Override
        public GraphPartitioner get() {
            if (served % numberOfRepeats == 0) {
                currentGraphPartitionerSupplier = partitionerSupplierIterator.next();
                served++;
                return currentGraphPartitionerSupplier.get();
            } else {
                served++;
                return currentGraphPartitionerSupplier.get();
            }
        }
    }

    public static class SingleShotStreamOrderSupplier implements Supplier<StreamOrder> {
        private List<Supplier<StreamOrder>> streamOrdersSuppliers = null;
        private Iterator<Supplier<StreamOrder>> streamOrderSupplierIterator = null;

        private int numberOfRepeats = 1;
        private int served = 0;

        public SingleShotStreamOrderSupplier(int numberOfRepeats) {
            this.numberOfRepeats = numberOfRepeats;

            streamOrdersSuppliers = new LinkedList<>();
            streamOrdersSuppliers.add(HdBfsStreamOrder::new);
            streamOrdersSuppliers.add(LdBfsStreamOrder::new);
            streamOrdersSuppliers.add(HdfStreamOrder::new);
            streamOrdersSuppliers.add(LdfStreamOrder::new);

            streamOrderSupplierIterator = streamOrdersSuppliers.iterator();
        }

        public int getLength() {
            return streamOrdersSuppliers.size() * numberOfRepeats;
        }

        public boolean hasNext() {
            return served < getLength();
        }

        public void reset() {
            served = 0;
        }

        @Override
        public StreamOrder get() {
            if (streamOrderSupplierIterator.hasNext()) {
                served++;
                return streamOrderSupplierIterator.next().get();
            } else {
                streamOrderSupplierIterator = streamOrdersSuppliers.iterator();
                return get();
            }
        }
    }

    public static class MultiShotStreamOrderSupplier implements Supplier<StreamOrder> {
        private List<Supplier<StreamOrder>> streamOrdersSuppliers = null;
        private Iterator<Supplier<StreamOrder>> streamOrderSupplierIterator = null;

        private int numberOfRepeats = 1;
        private int served = 0;

        public MultiShotStreamOrderSupplier(int numberOfRepeats) {
            this.numberOfRepeats = numberOfRepeats;

            streamOrdersSuppliers = new LinkedList<>();
            streamOrdersSuppliers.add(RandomStreamOrder::new);
            streamOrdersSuppliers.add(BfsStreamOrder::new);
            streamOrdersSuppliers.add(RandomFirstOrderStreamOrder::new);

            streamOrderSupplierIterator = streamOrdersSuppliers.iterator();
        }

        public int getLength() {
            return streamOrdersSuppliers.size() * numberOfRepeats;
        }

        public boolean hasNext() {
            return served < getLength();
        }

        public void reset() {
            served = 0;
        }

        @Override
        public StreamOrder get() {
            if (streamOrderSupplierIterator.hasNext()) {
                served++;
                return streamOrderSupplierIterator.next().get();
            } else {
                streamOrderSupplierIterator = streamOrdersSuppliers.iterator();
                return get();
            }
        }
    }

    public static class StreamOrderSupplier implements Supplier<StreamOrder> {
        private SingleShotStreamOrderSupplier singleExecStreamOrdersSupplier = null;
        private MultiShotStreamOrderSupplier multipleExecStreamOrdersSupplier = null;

        public StreamOrderSupplier(SingleShotStreamOrderSupplier singleShotStreamOrderSupplier, MultiShotStreamOrderSupplier multiShotStreamOrderSupplier) {
            this.singleExecStreamOrdersSupplier = singleShotStreamOrderSupplier;
            this.multipleExecStreamOrdersSupplier = multiShotStreamOrderSupplier;
        }

        public int getLength() {
            return singleExecStreamOrdersSupplier.getLength() + multipleExecStreamOrdersSupplier.getLength();
        }

        @Override
        public StreamOrder get() {
            if (singleExecStreamOrdersSupplier.hasNext()) {
                return singleExecStreamOrdersSupplier.get();
            }

            if (multipleExecStreamOrdersSupplier.hasNext()) {
                return multipleExecStreamOrdersSupplier.get();
            }

            singleExecStreamOrdersSupplier.reset();
            multipleExecStreamOrdersSupplier.reset();
            return get();
        }
    }


    public static class EvaluationResultSupplier implements Supplier<Tuple<BufferedWriter, EvaluationResult>> {
        private String inputSetName;
        private G graph;
        public BufferedWriter writer;

        private StreamOrderSupplier streamOrderSupplier = null;
        private GraphPartitionerSupplier graphPartitionersSupplier = null;

        private int length = 0;
        private int served = 0;

        public EvaluationResultSupplier(G graph, String inputSetName, String fileName, int numberOfRepeats, int numberOfPartitions) {
            this.inputSetName = inputSetName;
            this.graph = graph;

            streamOrderSupplier = new StreamOrderSupplier(
                    new SingleShotStreamOrderSupplier(1),
                    new MultiShotStreamOrderSupplier(numberOfRepeats)
            );

            graphPartitionersSupplier = new GraphPartitionerSupplier(numberOfPartitions, streamOrderSupplier.getLength());
            length = graphPartitionersSupplier.getLength();

            try {
                writer = new BufferedWriter(new FileWriter(fileName, true));
                writer.write("partitionerName,streamOrderName,datasetName,numberOfVertices,numberOfPartitions,partitionImbalance,edgeCut,replicationCost,timeTaken\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int getLength() {
            return length;
        }

        public float getPercentComplete() {
            return ((float)served) / length;
        }

        @Override
        public Tuple<BufferedWriter,EvaluationResult> get() {
            GraphPartitioner graphPartitioner = graphPartitionersSupplier.get();
            StreamOrder streamOrder = streamOrderSupplier.get();
            EvaluationResult result = Evaluator.evaluatePartitioner(graphPartitioner, streamOrder, this.graph, this.inputSetName);

            served++;

            System.out.println("Thread " + Thread.currentThread().getId() + " progress => " + getPercentComplete());
            return new Tuple<>(writer, result);
        }
    }
}
