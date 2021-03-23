import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import java.util.*;
import java.util.stream.IntStream;

public class Main {

    static BDDFactory bdd;

    public static void main(String[] args) {
        bdd = BDDFactory.init(1000, 1000);

        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 2));
        edges.add(new Edge(2, 1));
        edges.add(new Edge(1, 0));
        edges.add(new Edge(2, 3));
        graphToBDD(edges, 4).printDot();
    }


    public static BDD graphToBDD(List<Edge> edges, long verticesCount) {
        int v = log2(verticesCount);
        Map<Integer, Binary> intToBinary = initializeIntegerToBinaryMap(verticesCount, v);

        List<BDD> edgeBdds = new ArrayList<>();
        bdd.setVarNum(2 * v);
        for (Edge edge : edges) {
            Binary fromBinary = intToBinary.get(edge.from);
            Binary toBinary = intToBinary.get(edge.to);
            Binary edgeBinary = fromBinary.append(toBinary);

            BDD edgeBdd = IntStream.range(0, 2 * v)
                    .mapToObj(i -> edgeBinary.getIth(i) ? bdd.ithVar(i) : bdd.nithVar(i))
                    .reduce(bdd.one(), BDD::and);

            edgeBdds.add(edgeBdd);
        }

        return edgeBdds
                .stream()
                .reduce(bdd.zero(), BDD::or);
    }


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


    /**
     * Simple data structure for edges
     */
    private static class Edge {

        Edge(int from, int to) {
            this.from = from;
            this.to = to;
        }

        private int from;
        private int to;
    }


    /**
     * Wrapper for boolean list
     */
    private static class Binary {
        private final List<Boolean> bools;


        private Binary(List<Boolean> bools) {
            this.bools = bools;
        }

        public List<Boolean> getRaw() {
            return bools;
        }

        public boolean getIth(int i) {
            return bools.get(i);
        }

        /**
         * Ugly because java likes mutating things
         *
         * @param other binary
         * @return two Binaries appended into a new Binary object
         */
        public Binary append(Binary other) {
            List<Boolean> copy = new ArrayList<>(List.copyOf(bools));
            copy.addAll(other.getRaw());
            return new Binary(copy);
        }

        @Override
        public String toString() {
            return bools
                    .stream()
                    .map(n -> n ? "1" : "0")
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
        }

    }

    public static int log2(long N) {
        return (int) Math.ceil(Math.log(N) / Math.log(2));
    }
}
