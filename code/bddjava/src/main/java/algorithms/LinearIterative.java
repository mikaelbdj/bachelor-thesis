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
        loggingStrategy.logStarted("Linear iterative");
        BDD allNodes = graph.getNodes();
        bddStack.clear();
        FWSkel fwSkel = skelForward(graph, allNodes);
        Set<BDD> out = linear(graph, fwSkel.getSkel());
        loggingStrategy.logFinished("Linear iterative", out);
        return out;
    }

    private Set<BDD> linear(BddGraph graph, Skeleton sn) {
        BDD V = graph.getNodes();
        BDD S = sn.getS();
        BDD N = sn.getN();

        bddStack.push(V.id());
        bddStack.push(S.id());
        bddStack.push(N.id());

        Set<BDD> SCCs = new HashSet<>();

        while(!bddStack.empty()) {
            BDD currentN = bddStack.pop();
            BDD currentS = bddStack.pop();
            BDD currentV = bddStack.pop();

            if (currentV.isZero())
                continue;

            if (currentS.isZero())
                currentN = graph.pick(currentV);

            FWSkel newFWskel = skelForward(graph, currentN);
            BDD FW = newFWskel.getFW();
            Skeleton newSN = newFWskel.getSkel();
            BDD newS = newSN.getS();
            BDD newN = newSN.getN();

            BDD SCC = currentN.id();

            loggingStrategy.logSccFound(SCC);
            while (!(diff(graph.preImg(SCC).and(FW), SCC)).isZero()) {
                BDD newSCC = SCC.or(graph.preImg(SCC).and(FW));
                SCC.free();
                SCC = newSCC;
            }
            SCCs.add(SCC);

            BDD V_ = diff(currentV, FW);
            BDD S_ = diff(currentS, SCC);
            BDD N_ = graph.preImg(SCC.and(currentS)).and(diff(currentS, SCC));
            bddStack.push(V_);
            bddStack.push(S_);
            bddStack.push(N_);

            V_ = diff(FW, SCC);
            S_ = diff(newS, SCC);
            N_ = diff(newN, SCC);
            bddStack.push(V_);
            bddStack.push(S_);
            bddStack.push(N_);


        }

        return SCCs;
    }

    private static FWSkel skelForward(BddGraph graph, BDD N) {
        BDD L = N;
        Stack<BDD> stack = new Stack<>();
        BDD FW = graph.getBddFactory().zero();
        // forward set
        while (!L.isZero()) {
            stack.push(L);
            FW = FW.or(L);
            L = diff(graph.img(L), FW);
        }

        //skeleton of forward set

        L = stack.pop();
        BDD N_ = graph.pick(L);
        BDD S_ = N_;
        while (!stack.empty()) {
            L = stack.pop();
            S_ = S_.or(graph.pick(graph.preImg(S_).and(L)));
        }

        return new FWSkel(FW, new Skeleton(S_, N_));
    }

    private static BDD diff(BDD A, BDD B) {
        return A.and(B.not());
    }
}
