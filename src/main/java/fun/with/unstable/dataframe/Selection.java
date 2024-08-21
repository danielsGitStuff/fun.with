package fun.with.unstable.dataframe;

import fun.with.Lists;
import fun.with.Sets;
import fun.with.interfaces.actions.ActionBiPredicate;
import fun.with.interfaces.actions.ActionPredicate;
import fun.with.annotations.Unstable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Filters a {@link DataFrame} column and row wise.
 * Can produce a new {@link DataFrame} based on that by calling df().
 * Keep in mind that selectedColumns does limit df() to return just those columns.
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
     * @param predicate keeps the selected rows.
     * @return
     */
    public Selection filter(ActionPredicate<Lists<DFValue>> predicate) {
        List<Integer> newSelectedRows = new ArrayList<>();
        Lists<Integer> columnIndices = this.selectedColumnNames.map(c -> this.df.column2index.get(c));
        for (int rowIndex : this.selectedRows) {
            DFRow row = this.df.t.get(rowIndex);
            Lists<DFValue> selectedRow = columnIndices.map(row::get);
            boolean keep = predicate.test(selectedRow);
            if (keep)
                newSelectedRows.add(rowIndex);
        }
        return new Selection(this.df, this.selectedColumnNames, newSelectedRows);
    }

    /**
     * @param predicate keeps the selected rows.
     * @return
     */
    public Selection filterRows(ActionPredicate<DFRow> predicate) {
        List<Integer> newSelectedRows = new ArrayList<>();
        for (int rowIndex : this.selectedRows) {
            DFRow row = this.df.t.get(rowIndex);
            boolean keep = predicate.test(row);
            if (keep)
                newSelectedRows.add(rowIndex);
        }
        return new Selection(this.df, this.selectedColumnNames, newSelectedRows);
    }

    public Selection filterIndexed(ActionBiPredicate<Integer, Lists<DFValue>> predicate) {
        List<Integer> newSelectedRows = new ArrayList<>();
        Lists<Integer> columnIndices = this.selectedColumnNames.map(c -> this.df.column2index.get(c));
        for (int rowIndex : this.selectedRows) {
            DFRow row = this.df.t.get(rowIndex);
            Lists<DFValue> selectedRow = columnIndices.map(row::get);
            Boolean keep = predicate.test(rowIndex, selectedRow);
            if (keep)
                newSelectedRows.add(rowIndex);
        }
        return new Selection(this.df, this.selectedColumnNames, newSelectedRows);
    }

    /**
     * Produces a new {@link DataFrame} based on the current selectedRows.
     * This will keep only the selected or all (if not specified) columns.
     *
     * @return
     */
    public DataFrame df() {
        Lists<DFRow> t = Lists.empty();
        List<Integer> indices = new ArrayList<>();
        int idx = 0;
        Lists<Integer> selectedColumnIndices = this.selectedColumnNames.map(c -> this.df.column2index.get(c));
        Sets<String> selectedNoNumberColumns = this.selectedColumnNames.filter(c -> this.df.getNoNumberColumns().contains(c)).sets();
        for (int rowIndex : this.selectedRows) {
            DFRow row = this.df.t.get(rowIndex);
            Lists<Object> selectedValues = selectedColumnIndices.map(columnIdx -> row.get(columnIdx).getObject());
            t.add(new DFRow().setValues(selectedValues));
            indices.add(idx++);
        }
        DataFrame df = new DataFrame(t, indices).setColumns(selectedColumnNames).setNoNumberColumns(selectedNoNumberColumns);
        t.forEach(dfRow -> dfRow.setDf(df));
        return df;
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

    /**
     * Will keep the first rows which are unique by the given columns.
     * @param columns
     * @return
     */
    public Selection unique(String... columns) {
        Lists<Integer> filterColumnIndices = Lists.of(columns).map(s -> this.df.column2index.get(s));
        Sets<Integer> contentHashes = Sets.empty();
        List<Integer> selectedRows = new ArrayList<>();
        for (int rowIndex : this.selectedRows) {
            DFRow row = new DFRow();
            for (Integer columnIdx : filterColumnIndices.get()) {
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
