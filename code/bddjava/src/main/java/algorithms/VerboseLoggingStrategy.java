package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.Set;

public class VerboseLoggingStrategy implements LoggingStrategy {

    private int sccCount;
    private int nodesCovered;
    private int totalNodes;
    private LoggingStrategy nonVerboseLoggingStrategy;

    public VerboseLoggingStrategy(){
        sccCount = 0;
        nonVerboseLoggingStrategy = new NonVerboseLoggingStrategy();
    }

    @Override
    public void logSccFound(BDD scc) {
        nodesCovered += scc.satCount();
        System.out.println("SCC Found: " + scc + "\nTotal: " + ++sccCount);
        System.out.println("The found SCC contains " + scc.satCount() + " nodes.");
        System.out.println("It accounts for " + ((scc.satCount()/totalNodes) * 100) + "% of all (" + totalNodes + ") nodes.");
        System.out.println("So far " + nodesCovered + "/" + totalNodes  + " have been covered (" + ((nodesCovered/totalNodes) * 100) + "%).");
    }

    @Override
    public void logStarted(String algName) {
        nonVerboseLoggingStrategy.logStarted(algName);
    }

    @Override
    public void logFinished(String algName, Set<BDD> sccs) {
        nonVerboseLoggingStrategy.logFinished(algName, sccs);
    }

    @Override
    public void logStackSize(int stackSize) {
        System.out.println("Current stack size: "  + stackSize);
    }

    @Override
    public void logSymbolicStep(int step) {
        nonVerboseLoggingStrategy.logSymbolicStep(step);
    }

    @Override
    public void setBddGraph(BddGraph bddGraph) {

    }

    @Override
    public void setTotalNodes(int nodeCount) {
        this.totalNodes = nodeCount;
    }
}
