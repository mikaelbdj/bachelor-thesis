package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;

public class Lockstep implements GraphSCCAlgorithm{

    private final LoggingStrategy loggingStrategy;
    public Lockstep(LoggingStrategy loggingStrategy) {
        this.loggingStrategy = loggingStrategy;
    }

    public  Set<BDD> run(BddGraph bddGraph) {
        loggingStrategy.setBddGraph(bddGraph);
        loggingStrategy.setTotalNodes(((int) bddGraph.getNodes().satCount()));
        loggingStrategy.logStarted("Lockstep");
        BDD allNodes = bddGraph.getNodes();
        Set<BDD> out = lockstep(bddGraph, allNodes);
        loggingStrategy.logFinished("Lockstep", out);
        return out;
    }

    private Set<BDD> lockstep(BddGraph bddGraph, BDD P) {
        if (P.isZero()) return new HashSet<>();

        BDD F, B, FFront, BFront, C, converged;

        BDD v = bddGraph.pick(P);
        F = v.not().not();
        B = v.not().not();
        FFront = v.not().not();
        BFront = v.not().not();

        while (!FFront.isZero() && !BFront.isZero()) {
            FFront = extendFFrontier(bddGraph, P, FFront, F);
            BFront = extendBFrontier(bddGraph, P, BFront, B);
            BDD newF = F.or(FFront);
            F.free();
            F = newF;
            BDD newB = B.or(BFront);
            B.free();
            B = newB;
        }

        if (FFront.isZero()) {
            converged = F;
            while (!(BFront.and(F).isZero())) {
                BFront = extendBFrontier(bddGraph, P, BFront, B);
                B = B.or(BFront);
            }
        }
        else{
            converged = B;
            while (!(FFront.and(B).isZero())) {
                FFront = extendFFrontier(bddGraph, P, FFront, F);
                F = F.or(FFront);
            }
        }
        C = F.and(B);
        loggingStrategy.logSccFound(C);

        BDD rest1 = converged.and(C.not());
        BDD rest2 = P.and(converged.not());
        F.free();
        B.free();
        FFront.free();
        BFront.free();
        P.free();
        v.free();

        Set<BDD> SCCs1 = lockstep(bddGraph, rest1);
        Set<BDD> SCCs2 = lockstep(bddGraph, rest2);

        SCCs1.addAll(SCCs2);
        SCCs1.add(C);
        return SCCs1;
    }

    private static BDD extendFFrontier(BddGraph bddGraph, BDD P, BDD frontier, BDD current) {
        BDD frontierImg = bddGraph.img(frontier);
        BDD frontierImgAndP = frontierImg.and(P);
        BDD newFrontier = frontierImgAndP.and(current.not());
        frontierImg.free();
        frontierImgAndP.free();
        return newFrontier;
    }

    private static BDD extendBFrontier(BddGraph bddGraph, BDD P, BDD frontier, BDD current) {
        BDD frontierImg = bddGraph.preImg(frontier);
        BDD frontierImgAndP = frontierImg.and(P);
        BDD newFrontier = frontierImgAndP.and(current.not());
        frontierImg.free();
        frontierImgAndP.free();
        return newFrontier;
    }
}
