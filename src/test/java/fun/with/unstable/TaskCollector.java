package fun.with.unstable;

import fun.with.Lists;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;


public class TaskCollector<Start, Target> implements Collector<Object, Lists<Target>, Lists<Target>> {

    private final Lists<TaskFailure<Start>> failures = Lists.empty();

    public Lists<TaskFailure<Start>> getFailures() {
        return failures;
    }

    static void main() {
        Lists<Object> collect = Lists.of(1, 2, new TaskFailure<>(666, null), 3, 4)
                .stream()
                .parallel()
                .collect(new TaskCollector<>());
        System.out.println(collect);
    }


    @Override
    public Supplier<Lists<Target>> supplier() {
        System.out.println("TC creating supplier in thread " + Thread.currentThread().getName());
        return Lists::empty;
    }

    @Override
    public BiConsumer<Lists<Target>, Object> accumulator() {
        return (targets, o) -> {
            if (o instanceof TaskFailure<?>) {
                this.failures.add((TaskFailure<Start>) o);
            } else {
                targets.add((Target) o);
            }
        };
    }

    @Override
    public BinaryOperator<Lists<Target>> combiner() {
        return (targets, targets2) -> {
            targets.addAll(targets2);
            return targets;
        };
    }

    @Override
    public Function<Lists<Target>, Lists<Target>> finisher() {
        return null;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.IDENTITY_FINISH);
    }
}
