package bddgraph;

/**
 * Simple data structure for edges
 */
public class Edge {

    public Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }

    private final int from;
    private final int to;

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
