package algorithms;

import bddgraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.HashSet;
import java.util.Set;

public class LockstepWithTrimming implements GraphSCCAlgorithm {

    private final LoggingStrategy loggingStrategy;
    public LockstepWithTrimming(LoggingStrategy loggingStrategy) {
        this.loggingStrategy = loggingStrategy;
    }


    public Set<BDD> run(BddGraph bddGraph) {
        loggingStrategy.logStarted("Lockstep with trimming");
        BDD allNodes = bddGraph.getNodes();
        Set<BDD> out = lockstep(bddGraph, allNodes);
        loggingStrategy.logFinished("Lockstep with trimming");
        return out;
    }

    private Set<BDD> lockstep(BddGraph bddGraph, BDD P) {
        if (P.isZero()) return new HashSet<>();

        BDD F, B, FFront, BFront, C, converged;

        //Trimming
        Set<BDD> trimmedSingletonSCCs  = trim(P, bddGraph);

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

        if (FFront.isZero()) {
            converged = F;
            while (!(BFront.and(F).isZero())) {
                BFront = bddGraph.preImg(BFront).and(P).and(B.not());
                B = B.or(BFront);
            }
        } else {
            converged = B;
            while (!(FFront.and(B).isZero())) {
                FFront = bddGraph.img(FFront).and(P).and(F.not());
                F = F.or(FFront);
            }
        }

        C = F.and(B);
        loggingStrategy.logSccFound(C);

        Set<BDD> SCCs1 = lockstep(bddGraph, converged.and(C.not()));
        Set<BDD> SCCs2 = lockstep(bddGraph, P.and(converged.not()));

        SCCs1.addAll(SCCs2);
        SCCs1.addAll(trimmedSingletonSCCs);
        SCCs1.add(C);
        return SCCs1;
    }


    /**
     * @param noteSet this will be mutated
     * @param bddGraph graph
     * @return set of singleton SCCs that were trimmed away
     */
    private static Set<BDD> trim(BDD noteSet, BddGraph bddGraph) {
        Set<BDD> trimmed = new HashSet<>();
        BDD originalNoteSet = noteSet;
        BDD newNoteSet = noteSet;
        do {
            noteSet = newNoteSet;
            newNoteSet = noteSet.and(bddGraph.img(noteSet)).and(bddGraph.preImg(noteSet));
        } while (!newNoteSet.equals(noteSet));

        BDD difference = originalNoteSet.and(newNoteSet.not());
        while(!difference.isZero()) {
            BDD singletonSCC = bddGraph.pick(difference);
            trimmed.add(singletonSCC);
            difference = difference.and(singletonSCC.not());
        }

        return trimmed;
    }
}
