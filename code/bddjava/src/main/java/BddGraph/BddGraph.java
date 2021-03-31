package BddGraph;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDDPairing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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

    /**
     * Pick a node from the given set of nodes
     * More specifically finds one assignment of variables (in the context of this BddGraph) that satisfies the given BDD
     * @param noteSet some set of nodes
     * @return a node from the set of nodes
     */
    public BDD pick(BDD noteSet) {
        BDD result = noteSet;
        for (int i = 0; i < v; i++) {
            if (result.isZero()) {
                return bddFactory.zero();
            }
            BDD pos = result.and(bddFactory.ithVar(i));
            BDD neg = result.and(bddFactory.nithVar(i));
            if (neg.isZero()) {
                result = pos;
            }
            else {
                result = neg;
            }
        }

        return  result;
    }

    private BDD restrictAwayToVariables(BDD nodeSet) {
        for (int i = v; i < 2*v; i++) {
            BDD pos = nodeSet.restrict(bddFactory.ithVar(i));
            BDD neg = nodeSet.restrict(bddFactory.nithVar(i));
            nodeSet = pos.or(neg);
        }

        return nodeSet;
    }

    private BDD restrictAwayFromVariables(BDD nodeSet) {
        for (int i = 0; i < v; i++) {
            BDD pos = nodeSet.restrict(bddFactory.ithVar(i));
            BDD neg = nodeSet.restrict(bddFactory.nithVar(i));
            nodeSet = pos.or(neg);
        }

        return nodeSet;
    }

    public BDD img(BDD nodes) {
        //restrict to variables defined in nodes
        BDD img = bdd.and(nodes);

        img = restrictAwayFromVariables(img);

        BDDPairing pairing = bddFactory.makePair();
        for (int i = 0; i < v; i++) {
            pairing.set(i+v, i);
        }

        //rename variables, fx. if v = 2, rename 2->0, 3->1
        return img.replace(pairing);
    }

    public BDD preImg(BDD nodes) {
        //opposite of img

        BDDPairing pairing = bddFactory.makePair();
        for (int i = 0; i < v; i++) {
            pairing.set(i, i+v);
        }

        BDD replaced = nodes.replace(pairing);

        BDD preImg = bdd.and(replaced);

        return restrictAwayToVariables(preImg);
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

    public BDD getNodes() {
        BDD fromNodes = restrictAwayToVariables(bdd);
        BDD toNodes = restrictAwayFromVariables(bdd);

        BDDPairing pairing = bddFactory.makePair();
        for (int i = 0; i < v; i++) {
            pairing.set(i+v, i);
        }

        //rename variables, fx. if v = 2, rename 2->0, 3->1
        toNodes = toNodes.replace(pairing);

        return fromNodes.or(toNodes);
    }
}
