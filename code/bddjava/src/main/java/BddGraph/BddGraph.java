package BddGraph;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDDPairing;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A Bdd representing a graph - should have some fancy methods like img / preImg
 */
public class BddGraph {


    private final BDDFactory bddFactory = BDDFactory.init(1000, 1000);

    private Map<Integer, Binary> integerBinaryMap; // TODO: make singleton ?
    private final BDD bdd;
    private int v;

    public BddGraph(List<Edge> edges, int verticesCount) {
        v = log2(verticesCount);
        initializeIntegerToBinaryMap(verticesCount);
        bdd = graphToBDD(edges);
    }

    public BDD img(BDD nodes) {
        //restrict to variables defined in nodes
        BDD restricted = bdd.restrict(nodes);


        BDDPairing pairing = bddFactory.makePair();
        for (int i = 0; i < v; i++) {
            pairing.set(i+v, i);
        }

        //rename variables, fx. if v = 2, rename 2->0, 3->1
        return restricted.replace(pairing);
    }

    public BDD preImg(BDD nodes) {
        //opposite of img
        BDDPairing pairing = bddFactory.makePair();
        for (int i = 0; i < v; i++) {
            pairing.set(i, i+v);
        }

        BDD replaced = nodes.replace(pairing);

        return bdd.restrict(replaced);
    }

    //allow BddGraphs to create new BddGraphs from a bdd
    private BddGraph(BDD bdd) {
        this.bdd = bdd;
    }


    public BDD graphToBDD(List<Edge> edges) {
        bddFactory.extVarNum(2 * v);

        return edges
                .stream()
                .map(edge -> {
                    Binary edgeBinary = Binary.edgeToBinary(edge, integerBinaryMap);
                    return IntStream.range(0, 2 * v)
                            .mapToObj(i -> edgeBinary.getIth(i) ? bddFactory.ithVar(i) : bddFactory.nithVar(i))
                            .reduce(bddFactory.one(), BDD::and);
                })
                .reduce(bddFactory.zero(), BDD::or);
    }


    //initialize a map for converting integers to v-digit binary up to verticesCount
    private void initializeIntegerToBinaryMap(long verticesCount) {
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
        integerBinaryMap = binaryMap;
    }


    public static int log2(long N) {
        return (int) Math.ceil(Math.log(N) / Math.log(2));
    }

    public BDD getBdd() {
        return bdd;
    }

    public BDDFactory getBddFactory() {
        return bddFactory;
    }
}
