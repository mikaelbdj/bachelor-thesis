package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;

public class Linear implements GraphSCCAlgorithm{
    @Override
    public Set<BDD> run(BddGraph graph) {
        BDD allNodes = graph.getNodes();
        return linear(graph, N, S);
    }

    private static Set<BDD> linear(BddGraph graph, BDD N, BDD S) { // todo: add spineset as ??
        BDD V = graph.getNodes();
        BDD E = graph.getEdges();
        if (graph.getNodes().isZero())
            return new HashSet<>();
        //todo: if S=\empty
        BDD v = graph.pick(V);
        N= v;
        BDD SCC= v ;
        BDD FW = v;
        BDD C = v;

        //todo: implement skelForward() and fix FW

        while (!(diff(graph.preImg(SCC).and(FW), SCC)).isZero()) {
            SCC.orWith(graph.preImg(SCC).and(FW));
        }
        C.orWith(SCC);

        BDD V_ = diff(V, FW);


    }

    public static BDD diff(BDD A, BDD B) {
        return A.and(B.not());
    }
}
