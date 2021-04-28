package algorithms;

import net.sf.javabdd.BDD;

import java.util.Set;

public class NonVerboseLoggingStrategy implements LoggingStrategy {


    private long startTime;

    @Override
    public void logSccFound(BDD scc) {

    }

    @Override
    public void logStarted(String algName) {
        System.out.println("Starting algorithm: " + algName);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void logFinished(String algName, Set<BDD> out, int symbolicSteps) {
        System.out.println("Finished algorithm: " + algName);
        System.out.println("It took " + (System.currentTimeMillis() - startTime) + " ms");
    }

    @Override
    public void logStackSize(int stackSize) {

    }
}
