package fun.with.neurons;

import fun.with.Neurons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Neuron {
  private static final float NEURON_USED_FACTOR = 1.02f;
  
  public static float callActivationFunction(float x) {
    if (x < -10)
      return 0;
    else if (x > 10)
      return 1;
    else
      return (float) (1 / (1 + Math.exp(-x)));
  }
  
  public static final Random random = new Random(2);
  
  public static float randomWeight() {
    return (random.nextFloat() + 1) / 2;
  }
  
  protected List<Neuron> inputNeurons = new ArrayList<>();
  protected List<Float> inputWeights = new ArrayList<>();
  protected List<Neuron> outputNeurons = new ArrayList<>();
  
  protected float bias = 0f;
  
  protected float currentActivation = 0f;
  
  protected Integer inputPosition = null;
  
  public Neuron wireOutputTo(Neuron neuron) {
    neuron.inputNeurons.add(this);
    this.outputNeurons.add(neuron);
    return this;
  }
  
  public Neuron addInput(Neuron input) {
    if (input == this)
      return this;
    input.outputNeurons.add(this);
    this.inputNeurons.add(input);
    this.inputWeights.add(Neuron.randomWeight());
    return this;
  }
  
  public boolean hasInputNeurons() {
    return !this.inputWeights.isEmpty();
  }
  
  public float pull(Set<Neuron> visited, List<Float> xs) {
    visited.add(this);
    if (this.hasInputNeurons()) {
      float energy = 0f;
      for (int i = 0; i < this.inputNeurons.size(); i++) {
        Neuron n = this.inputNeurons.get(i);
        float weight = this.inputWeights.get(i);
        float energyFromNeuron = visited.contains(n) ? n.currentActivation : n.pull(visited, xs);
        energy += energyFromNeuron * weight;
        // update weight
        this.inputWeights.set(i, weight * NEURON_USED_FACTOR);
      }
      this.currentActivation = Neuron.callActivationFunction(energy);
//      System.out.println("e=" + energy + " -> " + this.currentActivation);
      return this.currentActivation;
    } else if (this.inputPosition != null) {
      return xs.get(this.inputPosition);
    } else {
      return this.bias;
    }
  }
}
