package fun.with.dataframe;

import fun.with.Lists;
import fun.with.Pair;
import fun.with.actions.ActionBiFunction;
import fun.with.actions.ActionBiPredicate;

import java.util.Objects;

public class DFRow {
    private DataFrame df;

    private Lists<Object> values = Lists.empty();

    DFRow() {
    }

    public DFRow setDf(DataFrame df) {
        this.df = df;
        return this;
    }

    public int size() {
        return this.values.size();
    }

    public DFRow setValues(Lists<Object> values) {
        this.values = values;
        return this;
    }

    public DFRow addValue(Object value) {
        this.values.add(value);
        return this;
    }

    public Object get(int columnIndex) {
        return this.values.get(columnIndex);
    }

    public Pair<ColumnCast, DFRow> cast() {
        Pair<ColumnCast, Lists<Object>> result = DataFrame.cast(this.values);
        return Pair.of(result.k(), new DFRow().setDf(this.df).setValues(result.v()));
    }

    public Lists<Object> filterIndexed(ActionBiPredicate<Integer, Object> f) {
        return this.values.filterIndexed(f);
    }

    public <X> Lists<X> mapIndexed(ActionBiFunction<Integer, Object, X> f) {
        return this.values.mapIndexed(f);
    }

    public int contentHash() {
        Object[] arr = new Object[this.values.size()];
        this.values.forEachIndexed((i, o) -> arr[i] = o);
        return Objects.hash(arr);
    }

    public Lists<Object> getValues() {
        return this.values;
    }
}
