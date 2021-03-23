package BddGraph;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * A Bdd representing a graph - should have some fancy methods like img / preimg
 */
public class BddGraph {


    private static final BDDFactory bddFactory = BDDFactory.init(1000, 1000);
    private final BDD bdd;

    public BddGraph(List<Edge> edges, int verticesCount) {
        bdd = graphToBDD(edges, verticesCount);
    }

    //allow BddGraphs to create new BddGraphs from a bdd
    private BddGraph(BDD bdd) {
        this.bdd = bdd;
    }


    public static BDD graphToBDD(List<Edge> edges, long verticesCount) {
        int v = log2(verticesCount);
        Map<Integer, Binary> intToBinary = initializeIntegerToBinaryMap(verticesCount, v);
        bddFactory.extVarNum(2 * v);

        return edges
                .stream()
                .map(edge -> {
                    Binary edgeBinary = Binary.edgeToBinary(edge, intToBinary);
                    return IntStream.range(0, 2 * v)
                            .mapToObj(i -> edgeBinary.getIth(i) ? bddFactory.ithVar(i) : bddFactory.nithVar(i))
                            .reduce(bddFactory.one(), BDD::and);
                })
                .reduce(bddFactory.zero(), BDD::or);
    }


    //initialize a map for converting integers to v-digit binary up to verticesCount
    private static Map<Integer, Binary> initializeIntegerToBinaryMap(long verticesCount, int v) {
        Map<Integer, Binary> binaryMap = new HashMap<>();
        for (int i = 0; i < verticesCount; i++) {
            String binaryString = Integer.toBinaryString(i);
            String zeroPaddedBinaryString = "0".repeat(v - binaryString.length()) + binaryString;
            List<Boolean> booleans = new ArrayList<>();
            for (int j = 0; j < v; j++) {
                if (zeroPaddedBinaryString.charAt(j) == '1') {
                    booleans.add(true);
                } else {
                    booleans.add(false);
                }
            }
            binaryMap.put(i, new Binary(booleans));
        }

        return binaryMap;
    }


    public static int log2(long N) {
        return (int) Math.ceil(Math.log(N) / Math.log(2));
    }

    public BDD getBdd() {
        return bdd;
    }
}
