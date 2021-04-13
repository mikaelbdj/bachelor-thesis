package Algorithms;

import BddGraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;

public class Linear implements GraphSCCAlgorithm{
    @Override
    public Set<BDD> run(BddGraph graph) {
        BDD allNodes = graph.getNodes();
        BDD N = allNodes;
        BDD S = allNodes;
        return linear(graph, N, S);
    }

    // todo: make a skeleton object?? to pass to linear and return in skeleton?

    private static Set<BDD> linear(BddGraph graph, BDD N, BDD S) { // todo: add spineset as ??
        BDD V = graph.getNodes();
        BDD E = graph.getEdges();

        if (V.isZero())
            return new HashSet<>();

        if (S.isZero())
            N = graph.pick(V);

        BDD FW = forward(N);
        BDD newS = skeleton(FW);
        BDD newN = skeleton(FW);

        BDD SCC = N ; //??

        while (!(diff(graph.preImg(SCC).and(FW), SCC)).isZero()) {
            SCC.orWith(graph.preImg(SCC).and(FW));
        }
        BDD C = N; //??
        C.orWith(SCC);

        BDD V_ = diff(V, FW);
        BDD E_ = E;
        BDD S_ = diff(S, SCC);
        BDD N_ = graph.preImg(SCC.and(S)).and(diff(S, SCC));
        BddGraph graph_ = graph; // todo: placeholder
        Set<BDD> SCCset1 = linear(graph_, S_, N_);

        V_ = diff(FW, SCC);
        E_ = E;
        S_ = diff(newS, SCC);
        N_ = diff(newN, SCC);
        graph_ = graph; // todo: placeholder
        Set<BDD> SCCset2 = linear(graph_, S_, N_);

        SCCset1.addAll(SCCset2);
        SCCset1.add(C);
        return SCCset1;
    }
    //todo: implement skelForward()
    private static BDD skeleton(BDD bdd) {
        return bdd;
    }
    private static BDD forward(BDD bdd) {
        return bdd;
    }

    private static BDD diff(BDD A, BDD B) {
        return A.and(B.not());
    }
}
