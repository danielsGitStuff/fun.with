package fun.with.neurons4.parts;

/**
 * Cell body
 */
public class Soma {
    protected float potential = .1f;

    public float getPotential() {
        return potential;
    }

    public Soma setPotential(float potential) {
        this.potential = potential;
        return this;
    }
}
