package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.Set;

public class NonVerboseLoggingStrategy implements LoggingStrategy {


    private long startTime;
    private int symbolicSteps;
    private int sccCount;

    @Override
    public void logSccFound(BDD scc) {

    }

    @Override
    public void logStarted(String algName) {
        System.out.println("Starting algorithm: " + algName);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void logFinished(String algName, Set<BDD> out) {
        System.out.println("Finished algorithm: " + algName);
        System.out.println("It took " + (System.currentTimeMillis() - startTime) + " ms");
        System.out.println(symbolicSteps + " symbolic steps used.");
        System.out.println(out.size() + " SCCs found.");
    }

    @Override
    public void logStackSize(int stackSize) {
    }

    @Override
    public void logSymbolicStep(int step) {
        symbolicSteps += step;
    }

    @Override
    public void setBddGraph(BddGraph bddGraph) {

    }
}
