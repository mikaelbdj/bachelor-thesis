package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Linear implements GraphSCCAlgorithm{

    private final LoggingStrategy loggingStrategy;
    public Linear(LoggingStrategy loggingStrategy) {
        this.loggingStrategy = loggingStrategy;
    }

    @Override
    public Set<BDD> run(BddGraph graph) {
        loggingStrategy.logStarted("Linear");
        BDD allNodes = graph.getNodes();
        FWSkel fwSkel = skelForward(graph, allNodes);
        Set<BDD> out = linear(graph, fwSkel.getSkel());
        loggingStrategy.logFinished("Linear", out);

        return out;
    }

    private Set<BDD> linear(BddGraph graph, Skeleton sn) {
        BDD V = graph.getNodes();
        BDD E = graph.getEdges();
        BDD S = sn.getS();
        BDD N = sn.getN();

        if (V.isZero())
            return new HashSet<>();

        if (S.isZero())
            N = graph.pick(V);

        FWSkel newFWskel = skelForward(graph, N);
        BDD FW = newFWskel.getFW();
        Skeleton newSN = newFWskel.getSkel();
        BDD newS = newSN.getS();
        BDD newN = newSN.getN();

        BDD SCC = N;

        loggingStrategy.logSccFound(SCC);
        while (!(diff(graph.preImg(SCC).and(FW), SCC)).isZero()) {
            SCC = SCC.or(graph.preImg(SCC).and(FW));
        }
        Set<BDD> C = new HashSet<>();
        C.add(SCC);

        BDD V_ = diff(V, FW);
        BDD E_ = E; //todo
        BDD S_ = diff(S, SCC);
        BDD N_ = graph.preImg(SCC.and(S)).and(diff(S, SCC));
        BddGraph graph_ = graph.newBddGraph(V_, E_);

        Skeleton sn_ = new Skeleton(S_, N_);
        Set<BDD> SCCset1 = linear(graph_, sn_);

        V_ = diff(FW, SCC);
        E_ = E; //todo
        S_ = diff(newS, SCC);
        N_ = diff(newN, SCC);
        graph_ = graph.newBddGraph(V_, E_);

        sn_ = new Skeleton(S_, N_);
        Set<BDD> SCCset2 = linear(graph_, sn_);

        SCCset1.addAll(SCCset2);
        SCCset1.addAll(C);
        return SCCset1;
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
