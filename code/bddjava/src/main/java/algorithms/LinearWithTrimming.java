package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class LinearWithTrimming implements GraphSCCAlgorithm{

    private final LoggingStrategy loggingStrategy;
    private final Stack<BDD> bddStack;

    public LinearWithTrimming(LoggingStrategy loggingStrategy) {
        this.loggingStrategy = loggingStrategy;
        bddStack = new Stack<>();
    }

    @Override
    public Set<BDD> run(BddGraph graph) {
        loggingStrategy.setBddGraph(graph);
        loggingStrategy.logStarted("Linear");
        BDD allNodes = graph.getNodes();
        bddStack.clear();
        Set<BDD> out = linear(graph, new Skeleton(graph.getBddFactory().zero(), graph.getBddFactory().zero()));
        loggingStrategy.logFinished("Linear", out);
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
        int loopCount = 0;
        while(!bddStack.empty()) {
            loopCount++;
            BDD currentE = bddStack.pop();
            BDD currentN = bddStack.pop();
            BDD currentS = bddStack.pop();
            BDD currentV = bddStack.pop();

            BddGraph currentGraph = graph.newBddGraph(currentV, currentE);

            if (loopCount == 1) {
                BDD newV = trim(currentV, currentGraph, SCCs);
                currentV.free();
                currentV = newV;

                BDD newE = currentGraph.restrictEdgesTo(currentV);
                currentE.free();
                currentE = newE;
                currentGraph = currentGraph.newBddGraph(currentV, currentE);

            }

            if (currentV.isZero())
                continue;

            if (currentS.isZero()) {
                currentN.free();
                currentN = currentGraph.pick(currentV);
            }

            FWSkel newFWskel = skelForward(currentGraph, currentN);
            BDD FW = newFWskel.getFW();
            Skeleton newSN = newFWskel.getSkel();
            BDD newS = newSN.getS();
            BDD newN = newSN.getN();

            BDD SCC = currentN.id();

            while (true) {
                BDD preAndFW = currentGraph.preImg(SCC).and(FW);
                loggingStrategy.logSymbolicStep(1);
                BDD notSCC = SCC.not();
                BDD preAndFWAndNotSCC = preAndFW.and(notSCC);
                if (preAndFWAndNotSCC.isZero()) {
                    break;
                }
                BDD newSCC = SCC.or(preAndFW);
                SCC.free();
                preAndFW.free();
                notSCC.free();
                preAndFWAndNotSCC.free();
                SCC = newSCC;
            }
            SCCs.add(SCC);
            loggingStrategy.logSccFound(SCC);

            BDD notSCC = SCC.not();

            BDD V_ = FW.and(notSCC);
            BDD S_ = newS.and(notSCC);
            BDD N_ = newN.and(notSCC);
            BDD E_ = currentGraph.restrictEdgesTo(V_);
            bddStack.push(V_);
            bddStack.push(S_);
            bddStack.push(N_);
            bddStack.push(E_);


            BDD notFW = FW.not();

            V_ = currentV.and(notFW);
            S_ = currentS.and(notSCC);
            BDD sccAndCurrentS = SCC.and(currentS);
            BDD sccAndCurrentSPreImg = currentGraph.preImg(sccAndCurrentS);
            N_ = sccAndCurrentSPreImg.and(S_);
            loggingStrategy.logSymbolicStep(1);
            E_ = currentGraph.restrictEdgesTo(V_);
            bddStack.push(V_);
            bddStack.push(S_);
            bddStack.push(N_);
            bddStack.push(E_);

            FW.free();
            newS.free();
            newN.free();
            currentS.free();
            currentN.free();
            currentV.free();
            notSCC.free();
            notFW.free();
            sccAndCurrentS.free();
            sccAndCurrentSPreImg.free();

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
            BDD newFW = FW.or(L);
            FW.free();
            FW = newFW;

            BDD imgL = graph.img(L);
            loggingStrategy.logSymbolicStep(1);
            BDD notFW = FW.not();
            L = imgL.and(notFW);
            notFW.free();
            imgL.free();
        }

        //skeleton of forward set

        L = stack.pop();
        BDD N_ = graph.pick(L);
        BDD S_ = N_.id();
        L.free();
        while (!stack.empty()) {
            L = stack.pop();
            BDD preImgS = graph.preImg(S_);
            BDD preImgSAndL = preImgS.and(L);
            BDD picked = graph.pick(preImgSAndL);
            BDD newS_ = S_.or(picked);
            S_.free();
            S_ = newS_;
            preImgS.free();
            preImgSAndL.free();
            picked.free();
            loggingStrategy.logSymbolicStep(1);
        }

        return new FWSkel(FW, new Skeleton(S_, N_));
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

        loggingStrategy.logSymbolicStep(2);

        BDD difference = originalNoteSet.and(newNodeSet.not());
        int trimCount = (int)(difference.satCount()/Math.pow(2, bddGraph.getV()));

        loggingStrategy.logString("Trimming found " + trimCount + " SCCs.");

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
