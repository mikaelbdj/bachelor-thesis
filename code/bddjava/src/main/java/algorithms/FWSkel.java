package algorithms;

import net.sf.javabdd.BDD;

public class FWSkel {
    public FWSkel(BDD FW, Skeleton SN) {
        this.FW = FW;
        this.SN = SN;
    }

    private final BDD FW;
    private final Skeleton SN;

    public BDD getFW() {
        return FW;
    }

    public Skeleton getSkel() {
        return SN;
    }

}
