package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.Set;

public interface LoggingStrategy {

    void logSccFound(BDD scc);
    void logStarted(String algName);
    void logFinished(String algName, Set<BDD> sccs);
    void logStackSize(int stackSize);
    void logSymbolicStep(int step);
    void setBddGraph(BddGraph bddGraph);
    default void logString(String s) {
        System.out.println(s);
    }
}
