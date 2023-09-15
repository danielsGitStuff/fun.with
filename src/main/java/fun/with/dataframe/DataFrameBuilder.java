package fun.with.dataframe;

import fun.with.Lists;
import fun.with.Maps;
import fun.with.Pair;
import fun.with.Sets;
import fun.with.annotations.Unstable;

@Unstable
public class DataFrameBuilder {
    private Lists<Lists<?>> t;
    private Lists<String> columns;
    private Integer rowSize = null;

    private Maps<String, Integer> column2index;

    private Sets<Integer> noNumberColumnIndices = Sets.empty();
    private Sets<String> noNumberColumns = Sets.empty();

    private DataFrameBuilder(Lists<Lists<? extends Object>> t) {
        Sets<Integer> differentRowLengths = t.map(Lists::size).sets();
        if (differentRowLengths.size() > 1) {
            throw new RuntimeException("All rows have to have the same length.");
        }
        this.t = t;
    }

    public static DataFrameBuilder wrap(Lists<Lists<? extends Object>> ls, Lists<String> columns) {
        return new DataFrameBuilder(ls).setColumns(columns);
    }

    public static DataFrameBuilder wrap(Lists<Lists<? extends Object>> ls) {
        return DataFrameBuilder.wrap(ls, ls.first().mapIndexed((integer, number) -> "Column" + integer));
    }

    public static DataFrameBuilder empty(Lists<String> columns) {
        return new DataFrameBuilder(Lists.empty()).setColumns(columns);
    }

    public DataFrameBuilder setColumns(Lists<String> columns) {
        this.columns = columns;
        this.column2index = this.columns.associateIndexed((integer, s) -> Pair.of(s, integer));
        return this;
    }

    public Lists<String> getColumns() {
        return columns;
    }

    public DataFrameBuilder feedRow(Lists<?> row) {
        if (this.rowSize != null && this.rowSize != row.size())
            throw new RuntimeException("Rows are expected to have " + this.rowSize + " columns. You tried to add one with " + row.size() + " columns");
        Lists<Object> castedRow = row.mapIndexed((index, o) -> this.noNumberColumnIndices.contains(index) ? o : Double.parseDouble(o.toString()));
        this.t.add(castedRow);
        return this;
    }

    public DataFrameBuilder setNoNumberColumns(String... columns) {
        return this.setNoNumberColumns(Sets.wrap(columns));
    }

    public DataFrameBuilder setNoNumberColumns(Sets<String> columns) {
        this.noNumberColumns.addAll(columns);
        for (String c : columns.get()) {
            if (!this.column2index.containsKey(c))
                throw new RuntimeException("Unknown column '" + c + "'");
            this.noNumberColumnIndices.add(this.column2index.get(c));
        }
        return this;
    }

    public DataFrame build() {
//        DataFrame df = new DataFrame(this.t)//
//                .setColumns(this.columns)//
//                .setColumn2index(this.column2index)//
//                .setNoNumberColumnIndices(this.noNumberColumnIndices)//
//                .setNoNumberColumns(this.noNumberColumns)//
//                .cast();
//        return df;
        return null;
    }
}
