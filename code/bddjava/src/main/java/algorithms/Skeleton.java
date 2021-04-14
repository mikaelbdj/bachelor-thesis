package algorithms;

import net.sf.javabdd.BDD;

public class Skeleton {
    public Skeleton(BDD S, BDD N) {
        this.S = S;
        this.N = N;
    }

    private final BDD S;
    private final BDD N;

    public BDD getS() {
        return S;
    }

    public BDD getN() {
        return N;
    }
}
