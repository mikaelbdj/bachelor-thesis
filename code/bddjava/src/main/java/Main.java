import algorithms.*;
import experiments.Experiment;
import experiments.Experiments;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        System.out.print("Datasets: ");
        System.out.println(arrayToString(DataSetExperiment.values()));
        System.out.print("Algorithms: ");
        System.out.println(arrayToString(AlgorithmType.values()));
        System.out.print("Logging types: ");
        System.out.println(arrayToString(LoggingType.values()));

        System.out.println("\nRun an experiment using the format:\ndataset algorithm logging\n");

        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();

        String[] inputs = input.split(" ");
        Experiment.ExperimentBuilder experimentBuilder = Arrays.stream(DataSetExperiment.values())
                .filter(dataSetExperiment -> dataSetExperiment.toString().equals(inputs[0]))
                .findFirst().get().getExperimentBuilder();
        LoggingStrategy loggingStrategy = Arrays.stream(LoggingType.values())
                .filter(loggingType -> loggingType.toString().equals(inputs[2]))
                .findFirst().get().getLoggingStrategy();
        GraphSCCAlgorithm graphSCCAlgorithm =  Arrays.stream(AlgorithmType.values())
                .filter(algorithmType -> algorithmType.toString().equals(inputs[1]))
                .findFirst().get().getAlgorithm().getDeclaredConstructor(LoggingStrategy.class).newInstance(loggingStrategy);

        Experiment experiment = experimentBuilder.setAlgorithm(graphSCCAlgorithm).build();
        experiment.run();
    }

    private static String arrayToString(Object[] array) {
        return Arrays.stream(array)
                .map(Object::toString)
                .map(name -> name + ", ")
                .reduce(String::concat).orElse("");
    }


    public enum DataSetExperiment {

        STANFORD("stanford", Experiments.buildStanford()),
        GNUTELLA("gnutella", Experiments.buildGnutellap2p04()),
        WIKIPEDIA_VOTES("wikipedia_votes", Experiments.buildWikipediaVotes());

        private final String name;
        private final Experiment.ExperimentBuilder experimentBuilder;

        DataSetExperiment(String name, Experiment.ExperimentBuilder experimentBuilder) {
            this.name = name;
            this.experimentBuilder = experimentBuilder;
        }


        public Experiment.ExperimentBuilder getExperimentBuilder() {
            return experimentBuilder;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum AlgorithmType {
        LINEAR("linear", Linear.class),
        LOCKSTEP("lockstep", Lockstep.class),
        LOCKSTEP_WITH_EDGE_RESTRICTION("lockstep_edge_restrict", LockstepWithEdgeRestriction.class),
        LOCKSTEP_WITH_EDGE_RESTRICTION_AND_TRIMMING("lockstep_edge_restrict_trim", LockstepWithEdgeRestrictionAndTrimming.class);


        private final String name;
        private final Class<? extends GraphSCCAlgorithm> algorithm;


        AlgorithmType(String name, Class<? extends GraphSCCAlgorithm> clazz) {
            this.name = name;
            this.algorithm = clazz;
        }

        @Override
        public String toString() {
            return name;
        }

        public Class<? extends GraphSCCAlgorithm> getAlgorithm() {
            return algorithm;
        }
    }

    public enum LoggingType {
        EXPLICIT("explicit", new ExplicitVerboseLoggingStrategy()),
        VERBOSE("verbose", new VerboseLoggingStrategy()),
        NON_VERBOSE("non_verbose", new NonVerboseLoggingStrategy()),
        NULL("null", new NullLoggingStrategy());

        private final String name;
        private final LoggingStrategy loggingStrategy;

        LoggingType(String name, LoggingStrategy loggingStrategy) {
            this.name = name;
            this.loggingStrategy = loggingStrategy;
        }

        @Override
        public String toString() {
            return name;
        }

        public LoggingStrategy getLoggingStrategy() {
            return loggingStrategy;
        }
    }
}
