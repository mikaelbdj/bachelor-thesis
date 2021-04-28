package algorithms;

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
    public void logFinished(String algName, Set<BDD> sccs, int symbolicSteps) {

    }

    @Override
    public void logStackSize(int stackSize) {

    }
}
