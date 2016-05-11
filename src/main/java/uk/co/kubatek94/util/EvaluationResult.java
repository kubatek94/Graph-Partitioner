package uk.co.kubatek94.util;

/**
 * Created by kubatek94 on 10/05/16.
 */
public class EvaluationResult {
    public String partitionerName;
    public String streamOrderName;
    public String datasetName;
    public String numberOfVertices;
    public String numberOfPartitions;
    public String partitionImbalance;
    public String edgeCut;
    public String replicationCost;
    public String timeTaken;

    public String toString() {
        return String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s",
                partitionerName, streamOrderName, datasetName, numberOfVertices,
                numberOfPartitions, partitionImbalance, edgeCut, replicationCost, timeTaken);
    }
}
