package Algorithms;

import BddGraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;

public class Linear implements GraphSCCAlgorithm{
    @Override
    public Set<BDD> run(BddGraph graph) {
        BDD allNodes = graph.getNodes();
        return linear(graph, allNodes);
    }

    private static Set<BDD> linear(BddGraph graph) { // todo: add spineset as ??
        BDD V = graph.getNodes();
        BDD E = graph.getEdges()
        if (graph.getNodes().isZero())
            return new HashSet<>();
        //todo: if S=\empty
        BDD v = graph.pick(V);
        BDD N, SCC, FW, C = v;

        //todo: implement skelForward() and fix FW

        while (!(diff(graph.preImg(SCC).and(FW), SCC)).isZero()) {
            SCC.orWith(graph.preImg(SCC).and(FW));
        }
        C.orWith(SCC);

        BDD V_ = diff(V, FW);


    }
    //A' = {x ∈ U : x ∉ A}.
    public BDD complement(BDD U, BDD A) {
        return U; //todo
    }
    //A – B = {x ∈ U : x ∈ A and  x ∉ B}= A ∩ B
    public BDD diff(BDD U, BDD A, BDD B) {
        return A.andWith(complement(U, B));
    }
}
