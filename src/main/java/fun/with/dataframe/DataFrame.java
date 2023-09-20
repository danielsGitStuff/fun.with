package fun.with.dataframe;

import fun.with.*;
import fun.with.annotations.Unstable;
import fun.with.interfaces.CollectionLike;
import org.w3c.dom.html.HTMLCollection;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Unstable
public class DataFrame {
    Lists<DFRow> t;
    Lists<String> columns;
    Integer rowSize;

    Maps<String, Integer> column2index;

    private Sets<Integer> noNumberColumnIndices = Sets.empty();
    private Sets<String> noNumberColumns = Sets.empty();
    private List<Integer> indices = new ArrayList<>();

    private List<ColumnCast> columnCasts = new ArrayList<>();

    /**
     * @param t each item in t is a row in a table
     */
    public static DataFrame fromLists(Lists<Lists<Object>> t) {
        return DataFrame.fromLists(t, Range.of(t.size()).ls().get());
    }

    public static DataFrame fromLists(Lists<Lists<Object>> t, List<Integer> indices) {
        Lists<DFRow> rows = t.map(ls -> new DFRow().setValues(ls));
        DataFrame df = new DataFrame(rows, indices);
        df.t.forEach(r -> r.setDf(df));
        df.initColumns();
        return df;
    }

    DataFrame(Lists<DFRow> rows, List<Integer> indices) {
        this.rowSize = DataFrame.checkTable(rows);
        Lists<DFRow> columnWise = DataFrame.transpose(rows);
        Lists<DFRow> castedColumnWise = Lists.empty();
        for (DFRow column : columnWise.get()) {
            Pair<ColumnCast, DFRow> casted = column.cast();
            castedColumnWise.add(casted.v());
            this.columnCasts.add(casted.k());
        }
        this.t = DataFrame.transpose(castedColumnWise);
        this.indices = indices;
        this.t.forEach(row -> row.setDf(this));
        this.initColumns();
    }

    private void initColumns() {
        this.columns = Range.of(this.rowSize).ls().map(i -> "Column" + i);
        this.column2index = this.columns.associateIndexed((idx, c) -> Pair.of(c, idx));
    }

    DataFrame(Lists<DFRow> rows) {
        this(rows, Range.of(rows.size()).ls().get());
    }

    public static DataFrame fromCsv(File csvFile, String delimiter) {
        Try.supply(() -> Files.readAllLines(csvFile.toPath()));
        DataFrame df = Try.with(() -> Files.readAllLines(csvFile.toPath())).function(strings -> {
                    Lists<Lists<Object>> ss = Lists.wrap(strings).map(s -> Lists.wrap(s.split(delimiter)).cast(Object.class));
                    Lists<String> columnNames = ss.first().map(Object::toString);
                    Lists<Lists<Object>> content = ss.drop(1);
                    content.map(os -> os.addAll(columnNames.size() - os.size() > 0 ? Range.of(columnNames.size() - os.size()).ls().map(x -> null) : Lists.empty())); // fill up missing values
                    DataFrame d = DataFrame.fromLists(content).setColumns(columnNames);
                    return d;
                }
        );
        return df;
    }

    /**
     * cast a column to boolean, int, double or string. In that order.
     *
     * @param column
     * @return a column cast to the first type that could cast all values
     */
    public static Pair<ColumnCast, Lists<Object>> cast(Lists<Object> column) {
        Lists<Object> casted = null;
        ColumnCast successfulCast = null;
        for (ColumnCast cast : ColumnCast.CASTS.get()) {
            try {
                casted = column.map(cast::apply);
                successfulCast = cast;
                break;
            } catch (RuntimeException e) {
                // can fail silently
            }
        }
        if (casted == null) {
            throw new RuntimeException("could not cast column");
        }
        return Pair.of(successfulCast, casted);
    }

    /**
     * @param t the table to transpose
     * @return Columns and rows flipped
     */
    private static Lists<DFRow> transpose(Lists<DFRow> t) {
        Integer rowSize = DataFrame.checkTable(t);
        Lists<DFRow> columnWise = Range.of(rowSize).ls().map(integer -> new DFRow());
        for (int columnIndex = 0; columnIndex < rowSize; columnIndex++) {
            for (int rowIndex = 0; rowIndex < t.size(); rowIndex++) {
                columnWise.get(columnIndex).addValue(t.get(rowIndex).getRaw(columnIndex));
            }
        }
        return columnWise;
    }

    public DataFrame transpose() {
        return new DataFrame(DataFrame.transpose(this.t));
    }

//    /**
//     * @param t the table to transpose
//     * @return Columns and rows flipped
//     */
//    private static Lists<Lists<Object>> transpose2(Lists<Lists<Object>> t) {
//        Integer rowSize = DataFrame.checkTable(t);
//        Lists<Lists<Object>> columnWise = Range.of(rowSize).ls().map(integer -> (Lists<Object>) Lists.empty());
//        for (int columnIndex = 0; columnIndex < rowSize; columnIndex++) {
//            for (int rowIndex = 0; rowIndex < t.size(); rowIndex++) {
//                columnWise.get(columnIndex).add(t.get(rowIndex).get(columnIndex));
//            }
//        }
//        return columnWise;
//    }

    /**
     * check whether all rows have the same amount of columns
     *
     * @param t
     * @return
     */
    private static Integer checkTable2(Lists<Lists<Object>> t) {
        Sets<Integer> differentRowLengths = t.map(Lists::size).sets();
        if (differentRowLengths.size() > 1) {
            throw new RuntimeException("All rows have to have the same length.");
        }
        if (!differentRowLengths.isEmpty())
            return differentRowLengths.iterator().next();
        return null;
    }

    /**
     * check whether all rows have the same amount of columns
     *
     * @param t
     * @return
     */
    private static Integer checkTable(Lists<DFRow> t) {
        Sets<Integer> differentRowLengths = t.map(DFRow::size).sets();
        if (differentRowLengths.size() > 1) {
            throw new RuntimeException("All rows have to have the same length.");
        }
        if (!differentRowLengths.isEmpty())
            return differentRowLengths.iterator().next();
        return null;
    }

    /**
     * Set column names. These can be used to identify get columns for later operations.
     *
     * @param columns
     * @return
     */
    public DataFrame setColumns(Lists<String> columns) {
        this.columns = columns;
        this.column2index = this.columns.associateIndexed((integer, s) -> Pair.of(s, integer));
        return this;
    }

    public DataFrame setColumns(String... columns) {
        return this.setColumns(Lists.wrap(columns));
    }

    public Lists<String> getColumns() {
        return columns;
    }

//    public DataFrame feedRow(Lists<Object> row) {
//        if (this.rowSize != null && this.rowSize != row.size())
//            throw new RuntimeException("Rows are expected to have " + this.rowSize + " columns. You tried to add one with " + row.size() + " columns");
//        this.t.add(row);
//        this.indices.add(this.indices.size());
//        return this;
//    }

    DataFrame setNoNumberColumnIndices(Sets<Integer> noNumberColumns) {
        this.noNumberColumnIndices = noNumberColumns;
        return this;
    }

    public DataFrame setNoNumberColumns(Sets<String> noNumberColumns) {
        this.noNumberColumns = noNumberColumns;
        return this;
    }

    public DataFrame setNoNumberColumns(String... noNumberColumns) {
        return this.setNoNumberColumns(Sets.wrap(noNumberColumns));
    }

    public Sets<Integer> getNoNumberColumnIndices() {
        return this.noNumberColumnIndices;
    }

    DataFrame setColumn2index(Maps<String, Integer> column2index) {
        this.column2index = column2index;
        return this;
    }

    DataFrame setRowSize(Integer rowSize) {
        this.rowSize = rowSize;
        return this;
    }

    public Selection select(String... columnNames) {
        Selection selection = Selection.of(this, columnNames);
        this.checkColumnNames(columnNames);
        return selection;
    }

    public Selection select(Integer... columnIndices) {
        Selection selection = Selection.of(this, Lists.wrap(columnIndices));
        return selection;
    }

    /**
     *
     * @param columnNames
     */
    public void checkColumnNames(String... columnNames) {
        for (String c : columnNames) {
            if (!this.column2index.containsKey(c))
                throw new RuntimeException("Unknown column '" + c + "'");
        }
    }

    public void checkColumnNames(CollectionLike<String, ?> columnNames) {
        for (String c : columnNames.getCollection()) {
            if (!this.column2index.containsKey(c))
                throw new RuntimeException("Unknown column '" + c + "'");
        }
    }

    public Sets<String> getNoNumberColumns() {
        return this.noNumberColumns;
    }

    public DataFrame cast() {
        return this;
    }

    public DataFrame drop(String... columns) {
        this.checkColumnNames(columns);
        Sets<String> columnsToKeep = this.columns.sets().subtract(Sets.wrap(columns));
        return this.keep(columnsToKeep);
    }

    public DataFrame keep(String... columns) {
        return this.keep(Sets.wrap(columns));
    }

    public DataFrame keep(Sets<String> columnsToKeepSet) {
        this.checkColumnNames(columns);
        Sets<Integer> columnIndicesToKeep = columnsToKeepSet.map(c -> this.column2index.get(c));
        Lists<String> columnsToKeep = this.columns.filter(columnsToKeepSet::contains);
        Sets<String> noNumberColumnsToKeep = this.noNumberColumns.filter(columnsToKeepSet::contains);
        Lists<DFRow> newT = Lists.empty();
        this.t.forEach(row -> newT.add(new DFRow().setDf(this).setValues(row.filterIndexed((integer, o) -> columnIndicesToKeep.contains(integer)))));
        return new DataFrame(newT).setColumns(columnsToKeep).setNoNumberColumns(noNumberColumnsToKeep);
    }

    public Lists<DFRow> getRows() {
        return this.t;
    }

    public DataFrame print() {
        Lists<Integer> columnCharCount = this.columns.map(String::length);
        String join = "| " + this.columns.map(c -> c + " | ").join("");
        System.out.println(Lists.of("-").repeat(join.length()).join(""));
        System.out.println(Lists.of("-").repeat(join.length()).join(""));
        System.out.println(join);
        System.out.println(Lists.of("-").repeat(join.length()).join(""));
        Lists<Integer> absIndices = Lists.empty();
        if (this.t.size() > 10) {
            Lists<Integer> first8 = this.t.take(8).mapIndexed((idx, ignore) -> idx);
            absIndices.addAll(first8);
            absIndices.add(this.t.size() - 1);
        } else {
            absIndices = Lists.wrap(this.indices);
        }
        absIndices.forEachIndexed((count, rowIndex) -> {
            DFRow row = this.t.get(rowIndex);
            String rowString = "| " + row.mapIndexed((colIdx, o) -> o.isNull() ? "null" : o.toString()).mapIndexed((colIdx, s) -> DataFrame.fillStr(s, columnCharCount.get(colIdx)) + " | ").join("");
            if (count == 8 && this.t.size() > 10 && join.length() > 5) {
                String extraLine = DataFrame.fillStr("|    ....", join.length() - 2);
                extraLine += "|";
                System.out.println(extraLine);
            }
            System.out.println(rowString);
        });
        System.out.println(Lists.of("-").repeat(join.length()).join(""));
        System.out.println(" -> " + this.t.size() + " rows");
        return this;
    }

    public static String fillStr(String s, int length) {
        if (s.length() > length) {
            s = s.substring(0, length);
        } else if (s.length() < length) {
            s += Lists.of(" ").repeat(length - s.length()).join("");
        }
        return s;
    }

    public Lists<Object> getColumn(String column) {
        Integer idx = this.column2index.get(column);
        return this.t.map(row -> row.get(idx));
    }

    public Lists<Object> getColumn(Integer idx) {
        return this.t.map(row -> row.get(idx));
    }
}
