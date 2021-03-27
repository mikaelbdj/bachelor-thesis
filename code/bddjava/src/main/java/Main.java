import BddGraph.BddGraph;
import BddGraph.Edge;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

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

        BDD node0 = graph.getBddFactory().nithVar(0).and(graph.getBddFactory().nithVar(1));
        BDD node1 = graph.getBddFactory().ithVar(0).and(graph.getBddFactory().nithVar(1));

        BDD img = graph.img(graph.img(node1));
        img.printSet();
    }
}
