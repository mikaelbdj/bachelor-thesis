package Algorithms;

import BddGraph.BddGraph;
import net.sf.javabdd.BDD;

import java.util.Set;

public interface GraphSCCAlgorithm {
    Set<BDD> run (BddGraph graph);
}
