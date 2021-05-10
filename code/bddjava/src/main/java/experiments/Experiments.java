package experiments;

import experiments.util.Constants;

public class Experiments {
    public static Experiment.ExperimentBuilder buildStanford() {
        String path = Constants.DATASETS_PATH + "web-Stanford.txt";
        int nodeAmount = 281903;
        return new Experiment.ExperimentBuilder()
                .setFilePath(path)
                .setNodeAmount(nodeAmount)
                .setNodesAreOneIndexed(true)
                .setBddNodeNum(10000000)
                .setBddCacheSize(100000);
    }

    public static Experiment.ExperimentBuilder buildGnutellap2p04() {
        String path = Constants.DATASETS_PATH + "p2p-Gnutella04.txt";
        int nodeAmount = 10879;
        return new Experiment.ExperimentBuilder()
                .setFilePath(path)
                .setNodeAmount(nodeAmount)
                .setNodesAreOneIndexed(false)
                .setBddNodeNum(5000000)
                .setBddCacheSize(100000);
    }

    public static Experiment.ExperimentBuilder buildCalls() {
        String path = Constants.DATASETS_PATH + "calls.txt";
        int nodeAmount = 400;
        return new Experiment.ExperimentBuilder()
                .setFilePath(path)
                .setNodeAmount(nodeAmount)
                .setNodesAreOneIndexed(false)
                .setBddNodeNum(5000000)
                .setBddCacheSize(100000);
    }

    public static Experiment.ExperimentBuilder buildWikipediaVotes() {
        String path = Constants.DATASETS_PATH + "wikipedia-votes.txt";
        int nodeAmount = 8298;
        return new Experiment.ExperimentBuilder()
                .setFilePath(path)
                .setNodeAmount(nodeAmount)
                .setNodesAreOneIndexed(true)
                .setBddNodeNum(5000000)
                .setBddCacheSize(100000);
    }
}
