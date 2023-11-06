package fun.with.dataframe;

import fun.with.Lists;
import fun.with.Sets;
import fun.with.actions.ActionPredicate;
import fun.with.annotations.Unstable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Filters a {@link DataFrame} column and row wise.
 * Can produce a new {@link DataFrame} based on that by calling df().
 * Keep in mind that selectedColumns does not limit df() to return just those columns.
 */
@Unstable
public class Selection {
    private final DataFrame df;

    private final Lists<String> selectedColumnNames;
    private final List<Integer> selectedRows;

    public static Selection of(DataFrame df, String... selectedColumns) {
        return Selection.of(df, Lists.of(selectedColumns), null);
    }

    public static Selection of(DataFrame df, Lists<Integer> selectedColumnIndices) {
        return new Selection(df, selectedColumnIndices.map(idx -> df.columns.get(idx)), null);
    }

    public static Selection of(DataFrame df, Lists<String> selectedColumnNames, List<Integer> selectedRows) {
        return new Selection(df, selectedColumnNames, selectedRows);
    }

    Selection(DataFrame df, Lists<String> selectedColumnNames, List<Integer> selectedRows) {
        this.df = df;
        this.selectedColumnNames = selectedColumnNames;
        if (selectedRows == null) {
            this.selectedRows = df.t.mapIndexed((integer, lists) -> integer).get();
        } else {
            this.selectedRows = selectedRows;
        }
    }

    /**
     * @param predicate takes the selected
     * @return
     */
    public Selection filter(ActionPredicate<Lists<DFValue>> predicate) {
        List<Integer> newSelectedRows = new ArrayList<>();
        Lists<Integer> columnIndices = this.selectedColumnNames.map(c -> this.df.column2index.get(c));
        for (int rowIndex : this.selectedRows) {
            DFRow row = this.df.t.get(rowIndex);
            Lists<DFValue> selectedRow = columnIndices.map(row::get);
            Boolean keep = predicate.test(selectedRow);
            if (keep)
                newSelectedRows.add(rowIndex);
        }
        return new Selection(this.df, this.selectedColumnNames, newSelectedRows);
    }

    /**
     * Produces a new {@link DataFrame} based on the current selectedRows.
     * This will keep all columns.
     *
     * @return
     */
    public DataFrame df() {
        Lists<DFRow> t = Lists.empty();
        List<Integer> indices = new ArrayList<>();
        int idx = 0;
        for (int rowIndex : this.selectedRows) {
            DFRow row = this.df.t.get(rowIndex);
            t.add(row);
            indices.add(idx++);
        }
        return new DataFrame(t, indices).setColumns(this.df.columns).setNoNumberColumns(this.df.getNoNumberColumns());
    }

    public Selection unique() {
        Sets<Integer> contentHashes = Sets.empty();
        List<Integer> selectedRows = new ArrayList<>();
        for (int rowIndex : this.selectedRows) {
            DFRow row = new DFRow();
            for (String column : this.selectedColumnNames.get()) {
                Integer columnIdx = this.df.column2index.get(column);
                DFValue o = this.df.t.get(rowIndex).get(columnIdx);
                row.addValue(o.getObject());
            }
            int contentHash = row.contentHash();
            if (!contentHashes.contains(contentHash)) {
                selectedRows.add(rowIndex);
            }
            contentHashes.add(contentHash);
        }
        return new Selection(this.df, this.selectedColumnNames, selectedRows);
    }

    public Selection sort(Comparator<Lists<DFValue>> comparator) {
        Lists<Integer> columnIndices = this.selectedColumnNames.map(c -> this.df.column2index.get(c));
        List<Integer> newSelectedRows = new ArrayList<>(this.selectedRows);
        newSelectedRows.sort((i1, i2) -> {
            DFRow row1 = Selection.this.df.getRows().get(i1);
            Lists<DFValue> values1 = columnIndices.map(row1::get);
            DFRow row2 = Selection.this.df.getRows().get(i2);
            Lists<DFValue> values2 = columnIndices.map(row2::get);
            return comparator.compare(values1, values2);
        });
        return new Selection(this.df, this.selectedColumnNames, newSelectedRows);
    }
}
