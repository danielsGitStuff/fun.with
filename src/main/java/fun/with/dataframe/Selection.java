package fun.with.dataframe;

import fun.with.Lists;
import fun.with.actions.ActionFunction;
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
            Lists<Object> row = this.df.t.get(rowIndex);
            Lists<Double> selectedRow = columnIndices.map(c-> (Double) NumberTransformer.DOUBLE.cast((Number) row.get(c)));
            Boolean keep = function.apply(selectedRow);
            if (keep)
                newSelectedRows.add(rowIndex);
        }
        Selection selection = new Selection(this.df, this.columnNames, newSelectedRows);
        return selection;
    }

    public DataFrame df() {
        Lists<Lists<Object>> t = Lists.empty();
        List<Integer> indices = new ArrayList<>();
        for (int rowIndex : this.selectedRows) {
            Lists<Object> row = this.df.t.get(rowIndex);
            t.add(row);
            indices.add(rowIndex);
        }
        return new DataFrame(t, indices).setColumns(this.df.columns);
    }
}
