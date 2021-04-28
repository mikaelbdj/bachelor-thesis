package experiments;

import algorithms.*;
import experiments.util.Constants;

public class Experiments {

    private static final GraphSCCAlgorithm VERBOSE_LOCKSTEP = new LockstepIterative(new VerboseLoggingStrategy());
    private static final GraphSCCAlgorithm VERBOSE_LINEAR = new Linear(new VerboseLoggingStrategy());
    private static final GraphSCCAlgorithm NON_VERBOSE_LOCKSTEP = new LockstepIterative(new NonVerboseLoggingStrategy());
    private static final GraphSCCAlgorithm NON_VERBOSE_LINEAR = new Linear(new NonVerboseLoggingStrategy());

    public final static Experiment STANFORD_LOCKSTEP = buildStanford(VERBOSE_LOCKSTEP);
    public final static Experiment STANFORD_LINEAR = buildStanford(VERBOSE_LINEAR);
    public final static Experiment GNUTELLA_04_LOCKSTEP = buildGnutellap2p04(VERBOSE_LOCKSTEP);
    public final static Experiment GNUTELLA_04_LINEAR = buildGnutellap2p04(VERBOSE_LINEAR);


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
}
