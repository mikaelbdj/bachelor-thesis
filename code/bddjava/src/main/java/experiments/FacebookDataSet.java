package experiments;

import algorithms.GraphSCCAlgorithm;
import bddgraph.BddGraph;
import bddgraph.Edge;
import net.sf.javabdd.BDD;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FacebookDataSet {

    public static void run(GraphSCCAlgorithm algorithm) {
        try (Stream<String> stream = Files.lines(Paths.get("datasets/facebook_combined.txt"))) {

            long startReading = System.currentTimeMillis();
            List<Edge> edges = stream
                    .map(line -> line.split(" "))
                    .map(lines -> new Edge(Integer.parseInt(lines[0]), Integer.parseInt(lines[1])))
                    .collect(Collectors.toList());

            long startCreatingGraph = System.currentTimeMillis();
            System.out.println("Finished reading file: " + (startCreatingGraph - startReading) + " ms");
            BddGraph faceBookCircles = new BddGraph(edges, 4039);

            long startFindingSCC = System.currentTimeMillis();
            System.out.println("Finished creating graph object: " + (startFindingSCC - startCreatingGraph) + " ms");

            Set<BDD> SCC = algorithm.run(faceBookCircles);

            long finishFindingSCC = System.currentTimeMillis();
            System.out.println("Finished finding SCCs: " + (finishFindingSCC - startFindingSCC) + " ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
