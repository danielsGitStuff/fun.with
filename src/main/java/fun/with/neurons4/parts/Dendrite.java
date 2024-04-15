package fun.with.neurons4.parts;

import fun.with.neurons4.Neuron;

import java.util.ArrayList;
import java.util.List;

/**
 * "Inputs"
 */
public class Dendrite {
    protected List<Synapse> synapses = new ArrayList<>();

    protected Neuron neuron;

    public List<Synapse> getSynapses() {
        return synapses;
    }

    public Dendrite(Neuron neuron) {
        this.neuron = neuron;
    }

    public Neuron getNeuron() {
        return neuron;
    }

    public Synapse createSynapse(Axon axon) {
        Synapse synapse = new Synapse(this, axon);
        synapses.add(synapse);
        return synapse;
    }
}
