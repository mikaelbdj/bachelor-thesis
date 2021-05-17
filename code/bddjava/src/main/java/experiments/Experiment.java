package experiments;

import algorithms.GraphSCCAlgorithm;
import bddgraph.BddGraph;
import bddgraph.Edge;
import experiments.util.ReadFile;
import net.sf.javabdd.BDD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Experiment {

    private final String filePath;
    private final int nodeAmount;
    private final GraphSCCAlgorithm algorithm;
    private final Boolean nodesAreOneIndexed;
    private final int bddNodeNum;
    private final int bddCacheSize;

    private Experiment(String filePath, int nodeAmount, GraphSCCAlgorithm algorithm, Boolean nodesAreOneIndexed, int nodeNum, int cacheSize) {
        this.filePath = filePath;
        this.nodeAmount = nodeAmount;
        this.algorithm = algorithm;
        this.nodesAreOneIndexed = nodesAreOneIndexed;
        this.bddNodeNum = nodeNum;
        this.bddCacheSize = cacheSize;
    }

    public Set<BDD> run() {
        long startReading = System.currentTimeMillis();
        try (Stream<Edge> stream = ReadFile.read(filePath, nodesAreOneIndexed)) {
            long startCreatingGraph = System.currentTimeMillis();
            List<Edge> edges = stream.collect(Collectors.toList());
            System.out.println("Finished reading file: " + (startCreatingGraph - startReading) + " ms");
            System.out.println("Found " + nodeAmount + " nodes and " + edges.size() + " edges in the graph, one_indexed = " + nodesAreOneIndexed);

            BddGraph graph = new BddGraph(edges, nodeAmount, bddNodeNum, bddCacheSize);

            long startFindingSCC = System.currentTimeMillis();
            System.out.println("Finished creating graph object: " + (startFindingSCC - startCreatingGraph) + " ms");

            return algorithm.run(graph);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class ExperimentBuilder {
        private String filePath;
        private Integer nodeAmount;
        private GraphSCCAlgorithm algorithm;
        private Boolean nodesAreOneIndexed;
        private int bddNodeNum = 1000000;
        private int bddCacheSize = 10000;

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

        public ExperimentBuilder setBddNodeNum(int bddNodeNum) {
            this.bddNodeNum = bddNodeNum;
            return this;
        }

        public ExperimentBuilder setBddCacheSize(int bddCacheSize) {
            this.bddCacheSize = bddCacheSize;
            return this;
        }

        public Experiment build() {
            if (filePath == null) throw new RuntimeException("Please provide a file path for an experiment");
            if (algorithm == null) throw new RuntimeException("Please provide a algorithm for an experiment");

            try {
                if (nodesAreOneIndexed == null) {
                    nodesAreOneIndexed = ReadFile.inferOneIndexed(filePath);
                }
                if (nodeAmount == null) {
                    nodeAmount = ReadFile.inferNodeAmount(filePath, nodesAreOneIndexed);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Experiment(filePath, nodeAmount, algorithm, nodesAreOneIndexed, bddNodeNum, bddCacheSize);
        }
    }
}
