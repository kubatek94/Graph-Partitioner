package uk.co.kubatek94.util;

import uk.co.kubatek94.dataset.Dataset;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * Created by kubatek94 on 11/05/16.
 */
public class EvaluationRunnable implements Runnable {
    private int numberOfPartitions;
    private int numberOfRepeats;
    private String fileName;
    private Stream<Dataset> queue;
    private LinkedList<Stream<EvaluationResult>> results = new LinkedList<>();

    public EvaluationRunnable(String fileName, Stream<Dataset> queue, int numberOfPartitions, int numberOfRepeats) {
        this.fileName = fileName;
        this.queue = queue;
        this.numberOfPartitions = numberOfPartitions;
        this.numberOfRepeats = numberOfRepeats;
    }

    public Stream<EvaluationResult> getResults() {
        return results.stream().flatMap(stream -> stream);
    }

    @Override
    public void run() {
        queue.forEach(dataset -> {
            Stream<EvaluationResult> result = Evaluator.evaluateDataset(dataset, numberOfPartitions, numberOfRepeats);
            results.addLast(result);
        });

        Path path = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("partitionerName, streamOrderName, datasetName, numberOfVertices, numberOfPartitions, partitionImbalance, edgeCut, replicationCost, timeTaken\n");

            getResults().forEach(result -> {
                try {
                    writer.write(result.toString());
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
