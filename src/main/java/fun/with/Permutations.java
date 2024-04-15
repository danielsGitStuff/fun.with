package fun.with;

import fun.with.misc.Range;

import java.util.ArrayList;
import java.util.List;

public class Permutations<T> {
    private final List<T> values;

    public Permutations(Lists<T> values) {
        this(values.get());
    }

    public Permutations(List<T> values) {
        this.values = values;
    }

    public Lists<Lists<T>> withRepetition(int k) {
        final int n = this.values.size();
        final int size = (int) java.lang.Math.pow(n, k);
        Lists<Lists<T>> result = Lists.wrap(new ArrayList<>((int) size));
        Integer[] wheelIndices = new Integer[k];
        Range.of(k).forEach(i -> wheelIndices[i] = 0);
        for (int ignored = 0; ignored < size; ignored++) {
            Lists<T> ls = Lists.of(wheelIndices).map(this.values::get);
            result.add(ls);
            for (int i = 0; i < k; i++) {
                int v = wheelIndices[i];
                if (v < n - 1) {
                    wheelIndices[i] = ++v;
                    break;
                } else {
                    wheelIndices[i] = 0;
                }
            }
        }
        return result;
    }

    public Lists<Lists<T>> withoutRepetition(int k) {
        throw new RuntimeException("not implemented yet");
    }
}
