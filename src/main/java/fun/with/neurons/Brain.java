package fun.with.neurons;

import fun.with.Lists;
import fun.with.Range;
import fun.with.TripleOf;
import fun.with.Triple;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class Brain {
  
  public static TripleOf<Float> createNeuronCoordinates(float radius) {
    float x = Neuron.random.nextFloat() - 0.5f;
    float y = Neuron.random.nextFloat() - 0.5f;
    float z = Neuron.random.nextFloat() - 0.5f;
    double mag = Math.sqrt(x * x + y * y + z * z);
    x /= mag;
    y /= mag;
    z /= mag;
    var d = Neuron.random.nextFloat() * radius;
    return Triple.ofSameKind(x * d, y * d, z * d);
  }
  
  private final int amountInputNeurons;
  private final int amountOutputNeurons;
  private Lists<SpatialNeuron> neurons = Lists.empty();
  private Lists<SpatialNeuron> brainInputNeurons = Lists.empty();
  private Lists<SpatialNeuron> brainOutputNeurons = Lists.empty();
  
  private final float radius;
  private final float neuronInputRadius = 4f;
  
  private final float maxConnectionChance = .8f, minConnectionChance = .1f;
  
  public Brain(float radius, int amountInputNeurons, int amountOutputNeurons) {
    this.radius = radius;
    this.amountInputNeurons = amountInputNeurons;
    this.amountOutputNeurons = amountOutputNeurons;
  }
  
  public Brain floodWithNeurons(List<SpatialNeuron> neurons) {
    this.neurons.addAll(neurons);
    return this;
  }
  
  public Brain floodWithNeurons(int amountOfNeurons) {
    { //todo own method
      final float z = -this.radius;
      final float y = 0f;
      final float maxRangeStart = this.radius / 2;
      final float stepSize = maxRangeStart / this.amountInputNeurons;
      Lists<Float> xs = Range.of(0f, maxRangeStart, stepSize).ls().drop(1).map(x -> x - (stepSize / 2));
      this.brainInputNeurons = xs.mapIndexed((index, x) -> {
        SpatialNeuron neuron = new SpatialNeuron(x, y, z);
        neuron.bias = 0f;
        neuron.inputPosition = index;
        return neuron;
      });
    }
    {
      final float z = 0f;
      final float y = 0f;
      final float maxRangeStart = this.radius / 2;
      final float stepSize = maxRangeStart / this.amountOutputNeurons;
      Lists<Float> xs = Range.of(0f, maxRangeStart, stepSize).ls().drop(1).map(x -> x - (stepSize / 2));
      this.brainOutputNeurons = xs.map(x -> new SpatialNeuron(x, y, z));
    }
    this.neurons = Range.of(amountOfNeurons + this.amountOutputNeurons).ls().map(integer -> new SpatialNeuron(Brain.createNeuronCoordinates(this.radius)));
    Lists<SpatialNeuron> availableAsInput = this.neurons.copy().addAll(this.brainInputNeurons);
    AtomicReference<Integer> connectionsMade = new AtomicReference<>(0);
    this.neurons.copy().addAll(this.brainOutputNeurons).forEach(n -> availableAsInput.forEach(available -> {
      if (available == n)
        return;
      float distance = available.distance(n);
      if (distance < this.neuronInputRadius) {
        float slope = (this.minConnectionChance - this.maxConnectionChance) / this.neuronInputRadius;
        float probOfConnection = maxConnectionChance - (slope * distance);
        float sample = Neuron.random.nextFloat();
        if (sample <= probOfConnection) {
          connectionsMade.getAndSet(connectionsMade.get() + 1);
          n.addInput(available);
        }
      }
    }));
    System.out.println(connectionsMade.get() + " neural connections made");
    return this;
  }
  
  public static void main(String[] args) {
    Brain brain = new Brain(10f, 2, 1);
    brain.floodWithNeurons(2000);
    final int BATCH_SIZE = 10;
    Lists<Lists<Float>> ands = Lists.of(Lists.of(1, 1).map(i -> (float) i)).repeat(BATCH_SIZE);
    Lists<Lists<Float>> nones = Lists.of(Lists.of(1, 1).map(i -> (float) i)).repeat(BATCH_SIZE);
    Lists<Lists<Lists<Float>>> noneBatches = Lists.of(nones).repeat(5);
    noneBatches.forEach(xsBatch -> {
      xsBatch.forEach(fs->{
        SpatialNeuron noneNeuron = brain.brainOutputNeurons.get(0);
        float result = noneNeuron.pull(new HashSet<>(), fs.get());
        System.out.println(result);
      });
    });
//    ands.forEach(fs -> {
//      SpatialNeuron oneNeuron = brain.brainOutputNeurons.get(1);
//      float result = oneNeuron.pull(new HashSet<>(), fs.get());
//      System.out.println(result);
//    });
  }
  
  //  private List<Float> feedForward(List<Float> xs) {
  //    Set<SpatialNeuron> visited = new HashSet<>();
  //    for (SpatialNeuron n : this.brainInputNeurons.get()) {
  //      visited.add(n);
  //      n.
  //    }
  //  }
}
