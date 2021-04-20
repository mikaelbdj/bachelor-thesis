package algorithms;

import net.sf.javabdd.BDD;

public interface LoggingStrategy {

    void logSccFound(BDD scc);
    void logStarted(String algName);
    void logFinished(String algName);
}
