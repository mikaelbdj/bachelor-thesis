package experiments;

import algorithms.GraphSCCAlgorithm;
import bddgraph.BddGraph;
import bddgraph.Edge;
import experiments.util.ReadFile;
import net.sf.javabdd.BDD;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Experiment {

    private final String filePath;
    private final int nodeAmount;
    private final GraphSCCAlgorithm algorithm;
    private final boolean nodesAreOneIndexed;

    private Experiment(String filePath, int nodeAmount, GraphSCCAlgorithm algorithm, boolean nodesAreOneIndexed) {
        this.filePath = filePath;
        this.nodeAmount = nodeAmount;
        this.algorithm = algorithm;
        this.nodesAreOneIndexed = nodesAreOneIndexed;
    }

    public void run() {
        long startReading = System.currentTimeMillis();
        try (Stream<Edge> stream = ReadFile.read(filePath, nodesAreOneIndexed)) {
            long startCreatingGraph = System.currentTimeMillis();
            System.out.println("Finished reading file: " + (startCreatingGraph - startReading) + " ms");
            BddGraph faceBookCircles = new BddGraph(stream.collect(Collectors.toList()), nodeAmount);

            long startFindingSCC = System.currentTimeMillis();
            System.out.println("Finished creating graph object: " + (startFindingSCC - startCreatingGraph) + " ms");

            Set<BDD> SCC = algorithm.run(faceBookCircles);

            long finishFindingSCC = System.currentTimeMillis();
            System.out.println("Finished finding SCCs: " + (finishFindingSCC - startFindingSCC) + " ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ExperimentBuilder{
        private String filePath;
        private Integer nodeAmount;
        private GraphSCCAlgorithm algorithm;
        private boolean nodesAreOneIndexed;

        public ExperimentBuilder setAlgorithm(GraphSCCAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public ExperimentBuilder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public ExperimentBuilder setNodeAmount(int nodeAmount) {
            this.nodeAmount = nodeAmount;
            return this;
        }

        public ExperimentBuilder setNodesAreOneIndexed(boolean nodesAreOneIndexed) {
            this.nodesAreOneIndexed = nodesAreOneIndexed;
            return this;
        }

        public Experiment build() {
            if (filePath == null) throw new RuntimeException("Please provide a file path for an experiment");
            if (algorithm == null) throw new RuntimeException("Please provide a algorithm for an experiment");
            if (nodeAmount == null) throw new RuntimeException("Please provide a node amount for an experiment");
            return new Experiment(filePath, nodeAmount, algorithm, nodesAreOneIndexed);
        }
    }
}