package algorithms;

import net.sf.javabdd.BDD;

import java.util.Set;

public class VerboseLoggingStrategy implements LoggingStrategy {

    private int sccCount;
    private LoggingStrategy nonVerboseLoggingStrategy;

    public VerboseLoggingStrategy(){
        sccCount = 0;
        nonVerboseLoggingStrategy = new NonVerboseLoggingStrategy();
    }

    @Override
    public void logSccFound(BDD scc) {
        System.out.println("SCC Found: " + scc + ", Total: " + ++sccCount);
    }

    @Override
    public void logStarted(String algName) {
        nonVerboseLoggingStrategy.logStarted(algName);
    }

    @Override
    public void logFinished(String algName, Set<BDD> sccs, int symbolicSteps) {
        nonVerboseLoggingStrategy.logFinished(algName, sccs, symbolicSteps);
    }

    @Override
    public void logStackSize(int stackSize) {
        System.out.println("Current stack size: "  + stackSize);
    }
}
