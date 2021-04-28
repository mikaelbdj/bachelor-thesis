package bddgraph;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDDPairing;

import java.util.*;
import java.util.stream.IntStream;

/**
 * A Bdd representing a graph - should have some fancy methods like img / preImg
 */
public class BddGraph {


    private BDDFactory bddFactory;

    private Map<Integer, Binary> integerBinaryMap; // TODO: make singleton ?
    private final BDD edges;
    private final BDD nodes;
    private final int v;



    public BddGraph(List<Edge> edges, int verticesCount, int nodeNum, int cacheSize) {
        bddFactory = BDDFactory.init(nodeNum, cacheSize);
        bddFactory.autoReorder(BDDFactory.REORDER_WIN2);
        bddFactory.reorderVerbose(0);
        v = log2(verticesCount);
        initializeIntegerToBinaryMap(verticesCount);
        this.edges = edgesToBDD(edges);
        nodes = generateNodes();
    }

    public BddGraph(List<Queue<Integer>> adjacencyLists, int nodeNum, int cacheSize) {
        bddFactory = BDDFactory.init(nodeNum, cacheSize);
        bddFactory.autoReorder(BDDFactory.REORDER_WIN2);
        bddFactory.reorderVerbose(0);
        v = log2(adjacencyLists.size());
        initializeIntegerToBinaryMap(adjacencyLists.size());
        edges = adjacencyListsToBDD(adjacencyLists);
        nodes = generateNodes();
    }

    private BddGraph(int v, BDD nodes, BDD edges, Map<Integer, Binary> integerBinaryMap, BDDFactory bddFactory) {
        this.v = v;
        this.nodes = nodes;
        this.edges = edges;
        this.integerBinaryMap = integerBinaryMap;
        this.bddFactory = bddFactory;

    }

    public BddGraph newBddGraph(BDD nodes, BDD edges) {
        return new BddGraph(v, nodes, edges, integerBinaryMap, bddFactory);
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
            BDD newNodeSet = union(pos, neg);
            nodeSet.free();
            nodeSet = newNodeSet;
        }

        return nodeSet;
    }

    private BDD restrictAwayFromVariables(BDD nodeSet) {
        for (int i = 0; i < v; i++) {
            BDD pos = nodeSet.restrict(bddFactory.ithVar(i));
            BDD neg = nodeSet.restrict(bddFactory.nithVar(i));
            BDD newNodeSet = union(pos, neg);
            nodeSet.free();
            nodeSet = newNodeSet;
        }

        return nodeSet;
    }

    public BDD img(BDD nodes) {
        //restrict to variables defined in nodes
        BDD img = edges.and(nodes);
        BDD restrictedImg = restrictAwayFromVariables(img);

        BDDPairing pairing = bddFactory.makePair();
        for (int i = 0; i < v; i++) {
            pairing.set(i+v, i);
        }

        //rename variables, fx. if v = 2, rename 2->0, 3->1
        BDD result =  restrictedImg.replace(pairing);
        restrictedImg.free();
        return result;
    }

    public BDD preImg(BDD nodes) {
        //opposite of img

        BDDPairing pairing = bddFactory.makePair();
        for (int i = 0; i < v; i++) {
            pairing.set(i, i+v);
        }

        BDD replaced = nodes.replace(pairing);

        BDD preImg = edges.and(replaced);
        BDD restrictedPreImg = restrictAwayToVariables(preImg);
        replaced.free();
        return restrictedPreImg;
    }

    private BDD edgesToBDD(List<Edge> edges) {
        bddFactory.extVarNum(2 * v);

        return edges
                .stream()
                .map(this::edgeToBDD)
                .reduce(bddFactory.zero(), BddGraph::union);
    }

    private BDD adjacencyListsToBDD(List<Queue<Integer>> adjacencyLists) {
        bddFactory.extVarNum(2 * v);

        return IntStream.range(0, adjacencyLists.size())
                .mapToObj(i -> {
                    Queue<Integer> adjacencyList = adjacencyLists.get(i);
                    return adjacencyList
                            .stream()
                            .map(j -> new Edge(i,j))
                            .map(this::edgeToBDD)
                            .reduce(bddFactory.zero(), BddGraph::union);
                })
                .reduce(bddFactory.zero(), BddGraph::union);
    }

    private BDD edgeToBDD(Edge edge) {
        Binary edgeBinary = Binary.edgeToBinary(edge, integerBinaryMap);
        return IntStream.range(0, 2 * v)
                .mapToObj(i -> edgeBinary.getIth(i) ? bddFactory.ithVar(i) : bddFactory.nithVar(i))
                .reduce(bddFactory.one(), BddGraph::intersection);
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

    public BDD getEdges() {
        return edges;
    }

    public BDDFactory getBddFactory() {
        return bddFactory;
    }

    private BDD generateNodes() {
        BDD nodes = bddFactory.zero();
        for (int j = 0; j < integerBinaryMap.size(); j++) {
            Binary number = integerBinaryMap.get(j);
            BDD node = IntStream.range(0,v)
                    .mapToObj(i -> number.getIth(i) ? bddFactory.ithVar(i) : bddFactory.nithVar(i))
                    .reduce(bddFactory.one(), BddGraph::intersection);
            nodes = union(nodes, node);
        }
        return nodes;
    }

    public BDD getNodes() {
        return nodes;
    }

    /**
     * Inefficient method for converting a bdd representing a nodeset into a set of integers
     * Don't use this for other things than converting node sets after running an algorithm..
     * @param nodeSet the node set
     * @return a set of integers
     */
    public Set<Integer> nodeSetToIntegerSet(BDD nodeSet) {
        Set<Integer> result = new HashSet<>();

        for (int j = 0; j < integerBinaryMap.size(); j++) {
            Binary number = integerBinaryMap.get(j);
            BDD bddNumber = IntStream.range(0,v)
                    .mapToObj(i -> number.getIth(i) ? bddFactory.ithVar(i) : bddFactory.nithVar(i))
                    .reduce(bddFactory.one(), BDD::and);
            if (nodeSet.restrict(bddNumber).isOne()) {
                result.add(j);
            }
        }

        return result;
    }


    public BDD restrictEdgesTo(BDD nodeSet) {
        BDDPairing pairing = bddFactory.makePair();
        for (int i = 0; i < v; i++) {
            pairing.set(i, i+v);
        }
        BDD renamedNodeSet  = nodeSet.replace(pairing);

        BDD restriction = edges.and(nodeSet).and(renamedNodeSet);

        return restriction;
    }

    public static BDD union (BDD... bdds) {
        BDD result = bdds[0];
        for (int i = 1; i < bdds.length; i++) {
            result = result.or(bdds[i]);
        }
        for (BDD bdd : bdds) {
            bdd.free();
        }

        return result;
    }

    public static BDD intersection (BDD... bdds) {
        BDD result = bdds[0];
        for (int i = 1; i < bdds.length; i++) {
            result = result.and(bdds[i]);
        }
        for (BDD bdd : bdds) {
            bdd.free();
        }

        return result;
    }
}
