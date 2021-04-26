import algorithms.GraphSCCAlgorithm;
import algorithms.Lockstep;
import algorithms.VerboseLoggingStrategy;
import experiments.Experiment;

public class Main {


    private static final String DATASETS_PATH = "datasets/";

    public static void main(String[] args) {
        GraphSCCAlgorithm lockstepWithVerboseLogging = new Lockstep(new VerboseLoggingStrategy());
        String stanfordWeblinksSetPath = DATASETS_PATH + "web-Stanford.txt";
        int stanfordWeblinksSetNodeAmount = 281903;
        Experiment stanfordWeblinks = new Experiment.ExperimentBuilder()
                .setAlgorithm(lockstepWithVerboseLogging)
                .setFilePath(stanfordWeblinksSetPath)
                .setNodeAmount(stanfordWeblinksSetNodeAmount)
                .setNodesAreOneIndexed(true)
                .build();

        stanfordWeblinks.run();
    }
}
