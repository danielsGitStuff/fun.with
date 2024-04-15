package fun.with.neurons4;

import fun.with.Lists;
import fun.with.misc.Range;
import fun.with.neurons4.parts.Axon;
import fun.with.neurons4.parts.Dendrite;

public class Brain {

    private final int noOfLayers, noOfInputs, noOfOutputs;

    public Brain(int noOfLayers, int noOfInputs, int noOfOutputs) {
        this.noOfLayers = noOfLayers;
        this.noOfInputs = noOfInputs;
        this.noOfOutputs = noOfOutputs;
    }

    public Brain build(){

        return this;
    }

    public static void main2(String[] args) {
        Lists<Axon> inputAxons = Range.of(0, 3).ls().map(x -> new Axon(null));
        Range.of(0, 3).forEach(layerIndex -> {
            Lists<Neuron> layer = Lists.empty();
            if (layerIndex == 0) {
                Range.of(3, 3).forEach(neuronIndex -> {
                    Neuron neuron = new Neuron();
                    layer.add(neuron);
                    Dendrite dendrite = neuron.createDendrite();
                    inputAxons.forEach(dendrite::createSynapse);
                });
            } else {
                Range.of(3, 3).forEach(neuronIndex -> {
                    Neuron neuron = new Neuron();

                });
            }
        });
        inputAxons.forEach(it -> it.activateExternally(1f));
    }
}
