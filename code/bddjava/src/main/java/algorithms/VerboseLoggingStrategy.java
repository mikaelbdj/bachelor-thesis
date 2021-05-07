package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.Set;

public class VerboseLoggingStrategy implements LoggingStrategy {

    private int sccCount;
    private int nodesCovered;
    private int totalNodes;
    private BddGraph bddGraph;
    private LoggingStrategy nonVerboseLoggingStrategy;

    public VerboseLoggingStrategy(){
        sccCount = 0;
        nonVerboseLoggingStrategy = new NonVerboseLoggingStrategy();
    }

    @Override
    public void logSccFound(BDD scc) {
        int sccNodeCount = (int)(scc.satCount()/Math.pow(2, bddGraph.getV()));
        nodesCovered += sccNodeCount;
        System.out.println("Found an SCC! Total: " + ++sccCount);
        System.out.println("The found SCC contains " + sccNodeCount + " node(s).");
        System.out.println("It accounts for " + (((double)sccNodeCount/totalNodes) * 100) + "% of all (" + totalNodes + ") node(s).");
        System.out.println("So far " + nodesCovered + "/" + totalNodes  + " have been covered (" + (((double)nodesCovered/totalNodes) * 100) + "%).\n");
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
        System.out.println("Current stack size: "  + stackSize + "\n");
    }

    @Override
    public void logSymbolicStep(int step) {
        nonVerboseLoggingStrategy.logSymbolicStep(step);
    }

    @Override
    public void setBddGraph(BddGraph bddGraph) {
        this.bddGraph = bddGraph;
        totalNodes = (int)(bddGraph.getNodes().satCount()/Math.pow(2, bddGraph.getV()));
    }
}
