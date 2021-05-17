package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class LockstepWithEdgeRestrictionAndTrimming implements GraphSCCAlgorithm {

    private final LoggingStrategy loggingStrategy;
    private final Stack<BDD> bddStack;

    public LockstepWithEdgeRestrictionAndTrimming(LoggingStrategy loggingStrategy) {
        this.loggingStrategy = loggingStrategy;
        bddStack = new Stack<>();
    }

    public Set<BDD> run(BddGraph bddGraph) {
        loggingStrategy.setBddGraph(bddGraph);
        loggingStrategy.logStarted("Lockstep with edge restriction and trimming");
        BDD allNodes = bddGraph.getNodes();
        bddStack.clear();
        Set<BDD> out = lockstep(bddGraph, allNodes);
        loggingStrategy.logFinished("Lockstep with edge restriction and trimming", out);
        return out;
    }


    private Set<BDD> lockstep(BddGraph bddGraph, BDD P) {
        bddStack.push(bddGraph.getEdges().id());
        bddStack.push(P.id());
        Set<BDD> SCCs = new HashSet<>();
        int loopCount = 0;
        while (!bddStack.empty()) {
            loopCount++;
            BDD currentP = bddStack.pop();
            BDD currentE = bddStack.pop();

            BddGraph currentGraph = bddGraph.newBddGraph(currentP, currentE);

            if (loopCount == 1) {
                BDD newP = trim(currentP, currentGraph, SCCs);
                currentP.free();
                currentP = newP;

                BDD newE = currentGraph.restrictEdgesTo(currentP);
                currentE.free();
                currentE = newE;
                currentGraph = currentGraph.newBddGraph(currentP, currentE);

            }
            if (currentP.isZero()) {
                continue;
            }

            BDD F, B, FFront, BFront, C, converged;

            BDD v = currentGraph.pick(currentP);
            F = v.id();
            B = v.id();
            FFront = v.id();
            BFront = v.id();

            while (!FFront.isZero() && !BFront.isZero()) {
                FFront = extendFFrontier(currentGraph, currentP, FFront, F);
                loggingStrategy.logSymbolicStep(1);
                BFront = extendBFrontier(currentGraph, currentP, BFront, B);
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
                    BFront = extendBFrontier(currentGraph, currentP, BFront, B);
                    loggingStrategy.logSymbolicStep(1);
                    BDD newB = B.or(BFront);
                    B.free();
                    B = newB;
                }
            } else {
                converged = B;
                while (!(FFront.and(B).isZero())) {
                    FFront = extendFFrontier(currentGraph, currentP, FFront, F);
                    loggingStrategy.logSymbolicStep(1);
                    BDD newF = F.or(FFront);
                    F.free();
                    F = newF;
                }
            }
            C = F.and(B);
            SCCs.add(C);
            loggingStrategy.logSccFound(C);

            BDD remainingNodes1 = converged.and(C.not());
            BDD remainingNodes2 = currentP.and(converged.not());
            BDD remainingEdges1 = bddGraph.restrictEdgesTo(remainingNodes1);
            BDD remainingEdges2 = bddGraph.restrictEdgesTo(remainingNodes2);

            F.free();
            B.free();
            FFront.free();
            BFront.free();
            v.free();
            currentP.free();
            currentE.free();

            bddStack.push(remainingEdges1);
            bddStack.push(remainingNodes1);

            bddStack.push(remainingEdges2);
            bddStack.push(remainingNodes2);

            loggingStrategy.logStackSize(bddStack.size());
        }
        P.free();
        return SCCs;
    }

    private static BDD extendFFrontier(BddGraph bddGraph, BDD P, BDD frontier, BDD current) {
        BDD frontierImg = bddGraph.img(frontier);
        BDD newFrontier = frontierImg.and(current.not());
        frontierImg.free();
        frontier.free();
        return newFrontier;
    }

    private static BDD extendBFrontier(BddGraph bddGraph, BDD P, BDD frontier, BDD current) {
        BDD frontierImg = bddGraph.preImg(frontier);
        BDD newFrontier = frontierImg.and(current.not());
        frontierImg.free();
        frontier.free();
        return newFrontier;
    }


    private BDD trim(BDD nodeSet, BddGraph bddGraph, Set<BDD> SCCs) {
        BDD originalNoteSet = nodeSet.id();
        BDD newNodeSet = nodeSet.id();
        BDD nodeSetCopy = nodeSet.id();

        nodeSetCopy.free();
        nodeSetCopy = newNodeSet;
        BDD nodeSetImg = bddGraph.img(nodeSetCopy);
        BDD nodeSetPreImg = bddGraph.preImg(nodeSetCopy);
        BDD preImgAndImg = nodeSetImg.and(nodeSetPreImg);
        newNodeSet = nodeSetCopy.and(preImgAndImg);
        nodeSetImg.free();
        nodeSetPreImg.free();
        preImgAndImg.free();

        BDD difference = originalNoteSet.and(newNodeSet.not());

        while (!difference.isZero()) {
            BDD singletonSCC = bddGraph.pick(difference);
            SCCs.add(singletonSCC);
            loggingStrategy.logSccFound(singletonSCC);
            BDD singletonNot = singletonSCC.not();
            BDD newDifference = difference.and(singletonNot);
            singletonNot.free();
            difference.free();
            difference = newDifference;
        }
        difference.free();

        return newNodeSet;
    }
}
