package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.Set;

public class ExplicitVerboseLoggingStrategy implements LoggingStrategy {

    private final LoggingStrategy verboseLoggingStrategy = new VerboseLoggingStrategy();
    private BddGraph bddGraph;
    private int sccCount;
    @Override
    public void logSccFound(BDD scc) {
        System.out.println("SCC Found: " + bddGraph.nodeSetToIntegerSet(scc) + ", Total: " + ++sccCount);
    }

    @Override
    public void logStarted(String algName) {
        verboseLoggingStrategy.logStarted(algName);
    }

    @Override
    public void logFinished(String algName, Set<BDD> sccs) {
        verboseLoggingStrategy.logFinished(algName, sccs);
    }

    @Override
    public void logStackSize(int stackSize) {
        verboseLoggingStrategy.logStackSize(stackSize);
    }

    @Override
    public void logSymbolicStep(int step) {
        verboseLoggingStrategy.logSymbolicStep(step);
    }

    @Override
    public void setBddGraph(BddGraph bddGraph) {
        this.bddGraph = bddGraph;
    }
}
