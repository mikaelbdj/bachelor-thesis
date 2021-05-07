package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.Set;

public class NullLoggingStrategy implements LoggingStrategy {
    @Override
    public void logSccFound(BDD scc) {

    }

    @Override
    public void logStarted(String algName) {

    }

    @Override
    public void logFinished(String algName, Set<BDD> sccs) {

    }

    @Override
    public void logStackSize(int stackSize) {

    }

    @Override
    public void logSymbolicStep(int step) {

    }

    @Override
    public void setBddGraph(BddGraph bddGraph) {

    }

    @Override
    public void setTotalNodes(int nodeCount) {

    }
}
