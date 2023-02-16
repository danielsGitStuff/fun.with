package fun.with.neurons;

import fun.with.Lists;
import fun.with.TripleOf;

public class SpatialNeuron extends Neuron {
  private final Lists<Float> coordinates;
  protected float x, y, z;
  
  public SpatialNeuron(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.bias = .1f;
    this.coordinates = Lists.of(x, y, z);
  }
  
  public SpatialNeuron(TripleOf<Float> triple) {
    this(triple.a(), triple.b(), triple.c());
  }
  
  public float distance(SpatialNeuron other) {
    float squaredDistance = this.coordinates.zip(other.coordinates).map(floatFloatPair -> {
      float e = floatFloatPair.k() - floatFloatPair.v();
      return e * e;
    }).fold(0f, Float::sum);
    return (float) Math.sqrt(squaredDistance);
  }
}
