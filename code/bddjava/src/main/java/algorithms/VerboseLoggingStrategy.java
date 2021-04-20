package algorithms;

import net.sf.javabdd.BDD;

public class VerboseLoggingStrategy implements LoggingStrategy {

    private int sccCount;
    private LoggingStrategy nonVerboseLoggingStrategy;

    public VerboseLoggingStrategy(){
        sccCount = 0;
        nonVerboseLoggingStrategy = new NonVerboseLoggingStrategy();
    }

    @Override
    public void logSccFound(BDD scc) {
        System.out.println("SCC Found: " + scc + ", Total: " + sccCount++);
    }

    @Override
    public void logStarted(String algName) {
        nonVerboseLoggingStrategy.logStarted(algName);
    }

    @Override
    public void logFinished(String algName) {
        nonVerboseLoggingStrategy.logFinished(algName);
    }
}
