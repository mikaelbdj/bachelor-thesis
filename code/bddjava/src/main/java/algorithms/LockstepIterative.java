package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class LockstepIterative implements GraphSCCAlgorithm{

    private final LoggingStrategy loggingStrategy;
    private final Stack<BDD> bddStack;

    public LockstepIterative(LoggingStrategy loggingStrategy) {
        this.loggingStrategy = loggingStrategy;
        bddStack = new Stack<>();
    }

    public  Set<BDD> run(BddGraph bddGraph) {
        loggingStrategy.logStarted("Lockstep iterative");
        BDD allNodes = bddGraph.getNodes();
        bddStack.clear();
        Set<BDD> out = lockstep(bddGraph, allNodes);
        loggingStrategy.logFinished("Lockstep iterative", out);
        return out;
    }



    private Set<BDD> lockstep(BddGraph bddGraph, BDD P) {
        bddStack.push(P.not().not());
        Set<BDD> SCCs = new HashSet<>();
        while(!bddStack.empty()) {
            BDD currentP = bddStack.pop();

            if (currentP.isZero()) {
                continue;
            }

            BDD F, B, FFront, BFront, C, converged;

            BDD v = bddGraph.pick(currentP);
            F = v.id();
            B = v.id();
            FFront = v.id();
            BFront = v.id();

            while (!FFront.isZero() && !BFront.isZero()) {
                FFront = extendFFrontier(bddGraph, currentP, FFront, F);
                loggingStrategy.logSymbolicStep(1);
                BFront = extendBFrontier(bddGraph, currentP, BFront, B);
                loggingStrategy.logSymbolicStep(1);
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
                    BFront = extendBFrontier(bddGraph, currentP, BFront, B);
                    loggingStrategy.logSymbolicStep(1);
                    BDD newB = B.or(BFront);
                    B.free();
                    B = newB;
                }
            }
            else{
                converged = B;
                while (!(FFront.and(B).isZero())) {
                    FFront = extendFFrontier(bddGraph, currentP, FFront, F);
                    loggingStrategy.logSymbolicStep(1);
                    BDD newF = F.or(FFront);
                    F.free();
                    F = newF;
                }
            }
            C = F.and(B);
            SCCs.add(C);
            loggingStrategy.logSccFound(C);

            BDD rest1 = converged.and(C.not());
            BDD rest2 = currentP.and(converged.not());
            F.free();
            B.free();
            FFront.free();
            BFront.free();
            v.free();
            currentP.free();

            bddStack.push(rest1);
            bddStack.push(rest2);

            loggingStrategy.logStackSize(bddStack.size());
        }
        P.free();
        return SCCs;
    }

    private static BDD extendFFrontier(BddGraph bddGraph, BDD P, BDD frontier, BDD current) {
        BDD frontierImg = bddGraph.img(frontier);
        BDD frontierImgAndP = frontierImg.and(P);
        BDD newFrontier = frontierImgAndP.and(current.not());
        frontierImg.free();
        frontierImgAndP.free();
        frontier.free();
        return newFrontier;
    }

    private static BDD extendBFrontier(BddGraph bddGraph, BDD P, BDD frontier, BDD current) {
        BDD frontierImg = bddGraph.preImg(frontier);
        BDD frontierImgAndP = frontierImg.and(P);
        BDD newFrontier = frontierImgAndP.and(current.not());
        frontierImg.free();
        frontierImgAndP.free();
        frontier.free();
        return newFrontier;
    }
}
