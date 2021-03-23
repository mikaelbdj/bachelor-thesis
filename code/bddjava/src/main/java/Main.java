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
        edges.add(new Edge(1,0));
        edges.add(new Edge(2,3));
        graphToBDD(edges, 4).printSet();
        System.out.println();
    }




    public static BDD graphToBDD(List<Edge> edges, long verticesCount) {
        int v = log2(verticesCount);
        Map<Integer, Binary> intToBinary = initializeIntegerToBinaryMap(verticesCount, v);

        List<BDD> edgeBdds = new ArrayList<>();
        bdd.setVarNum(2 * v);
        for (Edge edge : edges) {
            Binary fromBinary = intToBinary.get(edge.from);
            BDD fromBdd = IntStream.range(0, v)
                    .mapToObj(i -> fromBinary.getIth(i) ? bdd.ithVar(i) : bdd.nithVar(i))
                    .reduce(bdd.one(), BDD::and);

            Binary toBinary = intToBinary.get(edge.to);
            BDD toBdd = IntStream.range(0, v)
                    .mapToObj(i -> toBinary.getIth(i) ? bdd.ithVar(i+v) : bdd.nithVar(i+v))
                    .reduce(bdd.one(), BDD::and);

            BDD edgeBdd = fromBdd.and(toBdd);

            edgeBdds.add(edgeBdd);
        }

        return edgeBdds.stream().reduce(bdd.zero(), BDD::or);
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
        private final List<Boolean> number;


        private Binary(List<Boolean> number) {
            this.number = number;
        }

        public List<Boolean> getRaw() {
            return number;
        }

        public boolean getIth(int i) {
            return number.get(i);
        }

        @Override
        public String toString() {
            return number
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
