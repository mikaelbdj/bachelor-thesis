package Algorithms;

import BddGraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;

public class Lockstep {

    public static Set<BDD> run(BddGraph bddGraph) {
        BDD allNodes = bddGraph.getNodes();
        return lockstep(bddGraph, allNodes);
    }

    private static Set<BDD> lockstep(BddGraph bddGraph, BDD P) {
        if (P.isZero()) return new HashSet<>();

        BDD F, B, FFront, BFront, C, converged;

        BDD v = bddGraph.pick(P);
        F = v;
        B = v;
        FFront = v;
        BFront = v;

        while (!FFront.isZero() && !BFront.isZero()) {
            FFront = bddGraph.img(FFront).and(P).and(F.not());
            BFront = bddGraph.preImg(BFront).and(P).and(B.not());
            F = F.or(FFront);
            B = B.or(BFront);
        }

        if(FFront.isZero()) {
            converged = F;
        }
        else {
            converged = B;
        }

        while (!(BFront.and(F).isZero()) && !(FFront.and(B).isZero())) {
            FFront = bddGraph.img(FFront).and(P).and(F.not());
            BFront = bddGraph.preImg(BFront).and(P).and(B.not());
            F = F.or(FFront);
            B = B.or(BFront);
        }

        C = F.and(B);
        Set<BDD> SCCs1 = lockstep(bddGraph, converged.and(C.not()));
        Set<BDD> SCCs2 = lockstep(bddGraph, P.and(converged.not()));

        SCCs1.addAll(SCCs2);
        SCCs1.add(C);
        return SCCs1;
    }
}
