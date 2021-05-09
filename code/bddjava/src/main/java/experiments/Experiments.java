package experiments;

import algorithms.*;
import experiments.util.Constants;

public class Experiments {

    private static final GraphSCCAlgorithm VERBOSE_LOCKSTEP = new Lockstep(new VerboseLoggingStrategy());
    private static final GraphSCCAlgorithm VERBOSE_LINEAR = new Linear(new VerboseLoggingStrategy());
    private static final GraphSCCAlgorithm NON_VERBOSE_LOCKSTEP = new Lockstep(new NonVerboseLoggingStrategy());
    private static final GraphSCCAlgorithm NON_VERBOSE_LINEAR = new Linear(new NonVerboseLoggingStrategy());

    public final static Experiment STANFORD_LOCKSTEP = buildStanford(VERBOSE_LOCKSTEP);
    public final static Experiment STANFORD_LINEAR = buildStanford(VERBOSE_LINEAR);
    public final static Experiment GNUTELLA_04_LOCKSTEP = buildGnutellap2p04(VERBOSE_LOCKSTEP);
    public final static Experiment GNUTELLA_04_LINEAR = buildGnutellap2p04(VERBOSE_LINEAR);
    public final static Experiment WIKIPEDIA_VOTES_LOCKSTEP = buildWikipediaVotes(VERBOSE_LOCKSTEP);
    public final static Experiment WIKIPEDIA_VOTES_LINEAR = buildWikipediaVotes(VERBOSE_LINEAR);

    public final static Experiment CALLS_LINEAR = buildCalls(VERBOSE_LINEAR);
    public final static Experiment CALLS_LOCKSTEP = buildCalls(VERBOSE_LOCKSTEP);


    public static Experiment buildStanford(GraphSCCAlgorithm algorithm) {
        String path = Constants.DATASETS_PATH + "web-Stanford.txt";
        int nodeAmount = 281903;
        return new Experiment.ExperimentBuilder()
                .setAlgorithm(algorithm)
                .setFilePath(path)
                .setNodeAmount(nodeAmount)
                .setNodesAreOneIndexed(true)
                .setBddNodeNum(10000000)
                .setBddCacheSize(100000)
                .build();
    }

    public static Experiment buildGnutellap2p04(GraphSCCAlgorithm algorithm) {
        String path = Constants.DATASETS_PATH + "p2p-Gnutella04.txt";
        int nodeAmount = 10879;
        return new Experiment.ExperimentBuilder()
                .setAlgorithm(algorithm)
                .setFilePath(path)
                .setNodeAmount(nodeAmount)
                .setNodesAreOneIndexed(false)
                .setBddNodeNum(5000000)
                .setBddCacheSize(100000)
                .build();
    }

    public static Experiment buildCalls(GraphSCCAlgorithm algorithm) {
        String path = Constants.DATASETS_PATH + "calls.txt";
        int nodeAmount = 400;
        return new Experiment.ExperimentBuilder()
                .setAlgorithm(algorithm)
                .setFilePath(path)
                .setNodeAmount(nodeAmount)
                .setNodesAreOneIndexed(false)
                .setBddNodeNum(5000000)
                .setBddCacheSize(100000)
                .build();
    }

    public static Experiment buildWikipediaVotes(GraphSCCAlgorithm algorithm) {
        String path = Constants.DATASETS_PATH + "wikipedia-votes.txt";
        int nodeAmount = 8298;
        return new Experiment.ExperimentBuilder()
                .setAlgorithm(algorithm)
                .setFilePath(path)
                .setNodeAmount(nodeAmount)
                .setNodesAreOneIndexed(true)
                .setBddNodeNum(5000000)
                .setBddCacheSize(100000)
                .build();
    }
}
