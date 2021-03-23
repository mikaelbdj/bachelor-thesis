package BddGraph;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for boolean list
 */
public class Binary {
    private final List<Boolean> bools;


    public Binary(List<Boolean> bools) {
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

    public static Binary edgeToBinary(Edge edge, Map<Integer, Binary> intToBinary) {
        Binary fromBinary = intToBinary.get(edge.getFrom());
        Binary toBinary = intToBinary.get(edge.getTo());
        return fromBinary.append(toBinary);
    }

}