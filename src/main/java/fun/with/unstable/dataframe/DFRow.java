package fun.with.unstable.dataframe;

import fun.with.Lists;
import fun.with.misc.Pair;
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

    public DFValue get(int columnIndex) {
        return new DFValue(this, columnIndex, this.values.get(columnIndex));
    }

    public DFRow set(int columnIndex, Object value) {
        this.values.set(columnIndex, value);
        return this;
    }

    public DFValue get(String columnName) {
        this.df.checkColumnNames(columnName);
        int idx = this.df.column2index.get(columnName);
        return new DFValue(this, idx, this.values.get(idx));
    }

    public Object getRaw(int columnIndex) {
        return this.values.get(columnIndex);
    }

    public Pair<ColumnCast, DFRow> cast() {
        Pair<ColumnCast, Lists<Object>> result = DataFrame.cast(this.values);
        return Pair.of(result.k(), new DFRow().setDf(this.df).setValues(result.v()));
    }

    public Lists<Object> filterIndexed(ActionBiPredicate<Integer, DFValue> f) {
        return this.values.filterIndexed((integer, o) -> f.test(integer, new DFValue(this, integer, o)));
    }

    public <X> Lists<X> mapIndexed(ActionBiFunction<Integer, DFValue, X> f) {
        return this.values.mapIndexed((integer, o) -> f.apply(integer, new DFValue(this, integer, o)));
    }

    public int contentHash() {
        Object[] arr = new Object[this.values.size()];
        this.values.forEachIndexed((i, o) -> arr[i] = o);
        return Objects.hash(arr);
    }

    public Lists<DFValue> getValues() {
        return this.values.mapIndexed((integer, o) -> new DFValue(this, integer, o));
    }

    public DFValue last() {
        return new DFValue(this, this.values.size() - 1, this.values.last());
    }

    public DFValue first() {
        return new DFValue(this, 0, this.values.first());
    }

    @Override
    public String toString() {
        return this.values.join(" , ");
    }

    public DataFrame getDf() {
        return this.df;
    }
}
