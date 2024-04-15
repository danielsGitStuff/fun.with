package fun.with.neurons4;

import fun.with.neurons4.parts.Axon;
import fun.with.neurons4.parts.Dendrite;
import fun.with.neurons4.parts.Soma;

import java.util.ArrayList;
import java.util.List;

/***
 * // todo polyaxonal neurons exist
 */
public class Neuron {
    protected Soma soma = new Soma();
    protected Axon axon = new Axon(this);

    protected List<Dendrite> dendrites = new ArrayList<>();

    public Soma getSoma() {
        return soma;
    }

    public Axon getAxon() {
        return axon;
    }

    public List<Dendrite> getDendrites() {
        return dendrites;
    }

    public Neuron(){

    }

    public Dendrite createDendrite(){
        Dendrite dendrite = new Dendrite(this);
        this.dendrites.add(dendrite);
        return dendrite;
    }
}
