package fun.with.dataframe;

import fun.with.*;
import fun.with.annotations.Unstable;
import fun.with.interfaces.CollectionLike;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Unstable
public class DataFrame {
    Lists<Lists<Object>> t;
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
    public DataFrame(Lists<Lists<Object>> t) {
        this(t, Range.of(t.size()).ls().get());
    }

    DataFrame(Lists<Lists<Object>> t, List<Integer> indices) {
        this.rowSize = DataFrame.checkTable(t);
        Lists<Lists<Object>> columnWise = DataFrame.transpose(t);
        Lists<Lists<Object>> castedColumnWise = Lists.empty();
        for (Lists<Object> column : columnWise.get()) {
            Pair<ColumnCast, Lists<Object>> casted = DataFrame.cast(column);
            castedColumnWise.add(casted.v());
            this.columnCasts.add(casted.k());
        }
        this.t = DataFrame.transpose(castedColumnWise);
        this.indices = indices;
    }

    public static DataFrame fromCsv(File csvFile, String delimiter) {
        Try.supply(() -> Files.readAllLines(csvFile.toPath()));
        DataFrame df = Try.with(() -> Files.readAllLines(csvFile.toPath())).function(strings -> {
                    Lists<Lists<Object>> ss = Lists.wrap(strings).map(s -> Lists.wrap(s.split(delimiter)).cast(Object.class));
                    Lists<String> columnNames = ss.first().map(Object::toString);
                    Lists<Lists<Object>> content = ss.drop(1);
                    content.map(os -> os.addAll(columnNames.size() - os.size() > 0 ? Range.of(columnNames.size() - os.size()).ls().map(x -> null) : Lists.empty())); // fill up missing values
                    DataFrame d = new DataFrame(content).setColumns(columnNames);
                    return d;
                }
        );
        return df;
    }

    public static void main(String[] args) {
        Lists<Integer> ls = Range.of(1).ls();
        DataFrame df = DataFrame.fromCsv(new File("C:\\Users\\ddecker\\Downloads\\VS_Nachkommastaffeln.txt"), "\t");
        DataFrame nonNull = df.select("Preis").filter(vs -> vs.first().intValue() != 0).df();
        System.out.println("DataFrame.main");
    }

    /**
     * cast a column to boolean, int, double or string. In that order.
     *
     * @param column
     * @return a column cast to the first type that could cast all values
     */
    private static Pair<ColumnCast, Lists<Object>> cast(Lists<Object> column) {
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
    private static Lists<Lists<Object>> transpose(Lists<Lists<Object>> t) {
        Integer rowSize = DataFrame.checkTable(t);
        Lists<Lists<Object>> columnWise = Range.of(rowSize).ls().map(integer -> (Lists<Object>) Lists.empty());
        for (int columnIndex = 0; columnIndex < rowSize; columnIndex++) {
            for (int rowIndex = 0; rowIndex < t.size(); rowIndex++) {
                columnWise.get(columnIndex).add(t.get(rowIndex).get(columnIndex));
            }
        }
        return columnWise;
    }

    /**
     * check whether all rows have the same amount of columns
     *
     * @param t
     * @return
     */
    private static Integer checkTable(Lists<Lists<Object>> t) {
        Sets<Integer> differentRowLengths = t.map(Lists::size).sets();
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

    public DataFrame feedRow(Lists<Object> row) {
        if (this.rowSize != null && this.rowSize != row.size())
            throw new RuntimeException("Rows are expected to have " + this.rowSize + " columns. You tried to add one with " + row.size() + " columns");
        this.t.add(row);
        this.indices.add(this.indices.size());
        return this;
    }

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
        Selection selection = new Selection(this, Lists.wrap(columnNames));
        this.checkColumnNames(columnNames);
        return selection;
    }

    private void checkColumnNames(String[] columnNames) {
        for (String c : columnNames) {
            if (!this.column2index.containsKey(c))
                throw new RuntimeException("Unknown column '" + c + "'");
        }
    }

    private void checkColumnNames(CollectionLike<String, ?> columnNames) {
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
        Lists<Lists<Object>> newT = Lists.empty();
        this.t.forEach(row -> newT.add(row.filterIndexed((integer, o) -> columnIndicesToKeep.contains(integer))));
        return new DataFrame(newT).setColumns(columnsToKeep).setNoNumberColumns(noNumberColumnsToKeep);
    }

    public Lists<Lists<Object>> getRows() {
        return this.t;
    }

    public DataFrame print() {
        Lists<Integer> columnCharCount = this.columns.map(String::length);
        String join = "| " + this.columns.map(c -> c + " | ").join("");
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
            Lists<Object> row = this.t.get(rowIndex);
            String rowString = "| " + row.mapIndexed((colIdx, o) -> o == null ? "null" : o.toString()).mapIndexed((colIdx, s) -> DataFrame.fillStr(s, columnCharCount.get(colIdx)) + " | ").join("");
            if (count == 8 && this.t.size() > 10 && join.length() > 5) {
                String extraLine = DataFrame.fillStr("|    ....", join.length() - 2);
                extraLine += "|";
                System.out.println(extraLine);
            }
            System.out.println(rowString);
        });
        System.out.println(Lists.of("-").repeat(join.length()).join(""));
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
}
