import Algorithms.GraphSCCAlgorithm;
import Algorithms.Lockstep;
import BddGraph.BddGraph;
import BddGraph.Edge;
import net.sf.javabdd.BDD;

import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 2));
        edges.add(new Edge(2, 1));
        edges.add(new Edge(1, 0));
        edges.add(new Edge(2, 3));
        BddGraph graph = new BddGraph(edges, 4);
        GraphSCCAlgorithm algorithm = new Lockstep();
        System.out.println(algorithm.run(graph));
    }
}
