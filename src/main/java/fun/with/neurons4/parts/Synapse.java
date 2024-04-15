package fun.with.neurons4.parts;

import java.util.ArrayList;
import java.util.List;

/**
 * "Outputs"
 */
public class Synapse {
    protected Axon axon;
    protected Dendrite dendrite;
    protected float potential = 0f;
    protected float neurotransmitterLeft = 2f; // todo how much is there; does 100% potential transmit X% of what is left or X% of some portion or mixture of both concepts?
    protected List<Vesicle> fullVesicles = new ArrayList<>(); // related to above
    protected List<Vesicle> emptyVesicles = new ArrayList<>(); // related to above

    public Synapse(Dendrite dendrite, Axon axon) {
        this.dendrite = dendrite;
        this.axon = axon;
    }

    public float getPotential() {
        return potential;
    }
}
