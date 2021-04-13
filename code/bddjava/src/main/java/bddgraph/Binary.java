package bddgraph;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Wrapper for boolean list
 */
public class Binary {
    private final List<Boolean> bools;


    /**
     * @param bools boolean list to wrap
     */
    public Binary(List<Boolean> bools) {
        this.bools = bools;
    }

    /**
     * @param v length of boolean list
     */
    public Binary(int v) {
        this.bools = IntStream.range(0, v).mapToObj(i -> false).collect(Collectors.toList());
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
    private Binary append(Binary other) {
        List<Boolean> copy = new ArrayList<>(List.copyOf(bools));
        copy.addAll(bools);
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

    public static Binary edgeToBinary(Edge edge, Map<Integer, Binary> intToBinary) {
        Binary fromBinary = intToBinary.get(edge.getFrom());
        Binary toBinary = intToBinary.get(edge.getTo());
        return fromBinary.append(toBinary);
    }

}