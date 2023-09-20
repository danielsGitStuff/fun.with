package fun.with.dataframe;

import fun.with.Lists;
import fun.with.Sets;
import fun.with.actions.ActionFunction;
import fun.with.actions.ActionPredicate;
import fun.with.annotations.Unstable;
import fun.with.misc.NumberTransformer;

import java.util.ArrayList;
import java.util.List;

@Unstable
public class Selection {
    private final DataFrame df;

    private final Lists<String> columnNames;
    private final List<Integer> selectedRows;

    public Selection(DataFrame df, Lists<String> columnNames) {
        this(df, columnNames, null);
    }

    public Selection(DataFrame df, Lists<String> columnNames, List<Integer> selectedRows) {
        this.df = df;
        this.columnNames = columnNames;
        if (selectedRows == null) {
            this.selectedRows = df.t.mapIndexed((integer, lists) -> integer).get();
        } else {
            this.selectedRows = selectedRows;
        }
    }

    public Selection filter(ActionFunction<Lists<Double>, Boolean> function) {
        List<Integer> newSelectedRows = new ArrayList<>();
        Lists<Integer> columnIndices = this.columnNames.map(c -> this.df.column2index.get(c));
        for (int rowIndex : this.selectedRows) {
            DFRow row = this.df.t.get(rowIndex);
            Lists<Double> selectedRow = columnIndices.map(c -> (Double) NumberTransformer.DOUBLE.cast((Number) row.get(c)));
            Boolean keep = function.apply(selectedRow);
            if (keep)
                newSelectedRows.add(rowIndex);
        }
        Selection selection = new Selection(this.df, this.columnNames, newSelectedRows);
        return selection;
    }

    public Selection filterO(ActionPredicate<Lists<Object>> function) {
        List<Integer> newSelectedRows = new ArrayList<>();
        Lists<Integer> columnIndices = this.columnNames.map(c -> this.df.column2index.get(c));
        for (int rowIndex : this.selectedRows) {
            DFRow row = this.df.t.get(rowIndex);
            Lists<Object> selectedRow = columnIndices.map(row::get);
            Boolean keep = function.test(selectedRow);
            if (keep)
                newSelectedRows.add(rowIndex);
        }
        Selection selection = new Selection(this.df, this.columnNames, newSelectedRows);
        return selection;
    }

    public DataFrame df() {
        Lists<DFRow> t = Lists.empty();
        List<Integer> indices = new ArrayList<>();
        int idx = 0;
        for (int rowIndex : this.selectedRows) {
            DFRow row = this.df.t.get(rowIndex);
            t.add(row);
            indices.add(idx++);
        }
        return new DataFrame(t, indices).setColumns(this.df.columns);
    }

    public Selection unique() {
        Sets<Integer> contentHashes = Sets.empty();
        Lists<DFRow> t = Lists.empty();
        List<Integer> indices = new ArrayList<>();
        List<Integer> selectedRows = new ArrayList<>();
        for (int rowIndex : this.selectedRows) {
            DFRow row = new DFRow();
            for (String column : this.columnNames.get()) {
                Integer columnIdx = this.df.column2index.get(column);
                Object o = this.df.t.get(rowIndex).get(columnIdx);
                row.addValue(o);
            }
            int contentHash = row.contentHash();
            if (!contentHashes.contains(contentHash)) {
                selectedRows.add(rowIndex);
            }
            contentHashes.add(contentHash);
        }
        return new Selection(this.df, this.columnNames, selectedRows);
    }
}
