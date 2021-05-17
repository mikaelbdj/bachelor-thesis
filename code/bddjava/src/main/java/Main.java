import algorithms.*;
import experiments.Experiment;
import experiments.Experiments;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        initAlgorithms();
        initLoggingStrategies();
        final Map<String, List<String>> params = new HashMap<>();

        List<String> options = null;
        for (final String a : args) {
            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    System.err.println("Error at argument " + a);
                    return;
                }

                options = new ArrayList<>();
                params.put(a.substring(1), options);
            } else if (options != null) {
                options.add(a);
            } else {
                System.err.println("Illegal parameter usage");
                return;
            }
        }
        List<String> alg = params.get("a");
        if (alg == null) {
            System.err.println("No algorithm supplied");
            return;
        }
        List<String> fileName = params.get("f");
        if (fileName == null) {
            System.err.println("No file supplied");
            return;
        }
        List<String> logging = params.get("l");
        String loggingStr = logging == null ? "non_verbose" : logging.get(0);
        LoggingStrategy loggingStrategy = loggingStrategies.get(loggingStr);
        Function<LoggingStrategy, GraphSCCAlgorithm> algorithmFunc = algorithms.get(alg.get(0));
        if (algorithmFunc == null) {
            System.err.println("Invalid algorithm");
            return;
        }

        GraphSCCAlgorithm algorithm = algorithmFunc.apply(loggingStrategy);
        String fileNameStr = fileName.get(0);
        Experiment.ExperimentBuilder experimentBuilder = new Experiment.ExperimentBuilder();
        experimentBuilder.setAlgorithm(algorithm);
        experimentBuilder.setFilePath(fileNameStr);
        Experiment experiment = experimentBuilder.build();
        experiment.run();

    }

    private static final Map<String, Function<LoggingStrategy, GraphSCCAlgorithm>> algorithms = new HashMap<>();
    private static void initAlgorithms() {
        algorithms.put("lockstep", (loggingStrategy) -> new Lockstep(loggingStrategy));
        algorithms.put("linear", (loggingStrategy) -> new Linear(loggingStrategy));
        algorithms.put("lockstep_edge_restrict_with_trim", (loggingStrategy) -> new LockstepWithEdgeRestrictionAndTrimming(loggingStrategy));
        algorithms.put("lockstep_edge_restrict", (loggingStrategy) -> new LockstepWithEdgeRestriction(loggingStrategy));
    }

    private static final Map<String, LoggingStrategy> loggingStrategies = new HashMap<>();
    private static void initLoggingStrategies() {
        loggingStrategies.put("null", new NullLoggingStrategy());
        loggingStrategies.put("non_verbose", new NonVerboseLoggingStrategy());
        loggingStrategies.put("verbose", new VerboseLoggingStrategy());
        loggingStrategies.put("explicit", new ExplicitVerboseLoggingStrategy());
    }
}
