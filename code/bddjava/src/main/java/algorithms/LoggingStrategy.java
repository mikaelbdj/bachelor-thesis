package algorithms;

import net.sf.javabdd.BDD;

import java.util.Set;

public interface LoggingStrategy {

    void logSccFound(BDD scc);
    void logStarted(String algName);
    void logFinished(String algName, Set<BDD> sccs, int symbolicSteps);
    void logStackSize(int stackSize);
}
