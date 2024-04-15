package fun.with.neurons4.parts;

import fun.with.neurons4.Neuron;

import java.util.ArrayList;
import java.util.List;

public class Axon {
    protected Neuron neuron;
    protected List<Synapse> synapses = new ArrayList<>();

    public List<Synapse> getSynapses() {
        return synapses;
    }

    public Neuron getNeuron() {
        return neuron;
    }

    public Axon(Neuron neuron){
        this.neuron = neuron;
    }
    public void activateExternally(float potential){

    }
}
