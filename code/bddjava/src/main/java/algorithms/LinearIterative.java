package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class LinearIterative implements GraphSCCAlgorithm{

    private final LoggingStrategy loggingStrategy;
    private final Stack<BDD> bddStack;

    public LinearIterative(LoggingStrategy loggingStrategy) {
        this.loggingStrategy = loggingStrategy;
        bddStack = new Stack<>();
    }

    @Override
    public Set<BDD> run(BddGraph graph) {
        loggingStrategy.setBddGraph(graph);
        loggingStrategy.logStarted("Linear iterative");
        BDD allNodes = graph.getNodes();
        bddStack.clear();
        Set<BDD> out = linear(graph, new Skeleton(graph.getBddFactory().zero(), graph.getBddFactory().zero()));
        loggingStrategy.logFinished("Linear iterative", out);
        return out;
    }

    private Set<BDD> linear(BddGraph graph, Skeleton sn) {
        BDD V = graph.getNodes();
        BDD E = graph.getEdges();
        BDD S = sn.getS();
        BDD N = sn.getN();

        bddStack.push(V.id());
        bddStack.push(S.id());
        bddStack.push(N.id());
        bddStack.push(E.id());

        Set<BDD> SCCs = new HashSet<>();

        while(!bddStack.empty()) {
            BDD currentE = bddStack.pop();
            BDD currentN = bddStack.pop();
            BDD currentS = bddStack.pop();
            BDD currentV = bddStack.pop();

            BddGraph currentGraph = graph.newBddGraph(currentV, currentE);

            if (currentV.isZero())
                continue;

            if (currentS.isZero())
                currentN = currentGraph.pick(currentV);

            FWSkel newFWskel = skelForward(currentGraph, currentN);
            BDD FW = newFWskel.getFW();
            Skeleton newSN = newFWskel.getSkel();
            BDD newS = newSN.getS();
            BDD newN = newSN.getN();

            BDD SCC = currentN.id();

            while (true) {
                BDD preAndFW = currentGraph.preImg(SCC).and(FW);
                loggingStrategy.logSymbolicStep(1);
                if ((diff(preAndFW, SCC)).isZero()) {
                    break;
                }
                SCC = SCC.or(preAndFW);
                //SCC.free();
                //SCC = newSCC;
            }
            SCCs.add(SCC);
            loggingStrategy.logSccFound(SCC);

            BDD V_ = diff(FW, SCC);
            BDD S_ = diff(newS, SCC);
            BDD N_ = diff(newN, SCC);
            BDD E_ = currentGraph.restrictEdgesTo(V_);
            bddStack.push(V_);
            bddStack.push(S_);
            bddStack.push(N_);
            bddStack.push(E_);


            V_ = diff(currentV, FW);
            S_ = diff(currentS, SCC);
            N_ = currentGraph.preImg(SCC.and(currentS)).and(diff(currentS, SCC));
            loggingStrategy.logSymbolicStep(1);
            E_ = currentGraph.restrictEdgesTo(V_);
            bddStack.push(V_);
            bddStack.push(S_);
            bddStack.push(N_);
            bddStack.push(E_);

        /*    FW.free();
            newS.free();
            newN.free();
            currentS.free();
            currentN.free();
            currentV.free();*/
            loggingStrategy.logStackSize(bddStack.size());
        }

        return SCCs;
    }

    private FWSkel skelForward(BddGraph graph, BDD N) {
        BDD L = N.id();
        Stack<BDD> stack = new Stack<>();
        BDD FW = graph.getBddFactory().zero();
        // forward set
        while (!L.isZero()) {
            stack.push(L);
            FW = FW.or(L);

            BDD imgL = graph.img(L);
            loggingStrategy.logSymbolicStep(1);
            L = diff(imgL, FW);
        }

        //skeleton of forward set

        L = stack.pop();
        BDD N_ = graph.pick(L);
        BDD S_ = N_.id();
        while (!stack.empty()) {
            L = stack.pop();
            BDD preimgS = graph.preImg(S_);
            S_ = S_.or(graph.pick(preimgS.and(L)));
            loggingStrategy.logSymbolicStep(1);
            //S_.free();
            //S_ = newS_;
            //preimgS.free();
        }

        return new FWSkel(FW, new Skeleton(S_, N_));
    }

    private static BDD diff(BDD A, BDD B) {
        return A.and(B.not());
    }
}
