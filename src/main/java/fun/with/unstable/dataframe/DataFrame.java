package fun.with.unstable.dataframe;

import fun.with.*;
import fun.with.annotations.Unstable;
import fun.with.interfaces.CollectionLike;
import fun.with.interfaces.actions.ActionConsumer;
import fun.with.interfaces.actions.ActionFunction;
import fun.with.misc.Checks;
import fun.with.misc.Pair;
import fun.with.Ranges;
import fun.with.misc.Strings;
import fun.with.misc.TextReader;
import fun.with.unstable.Try;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Unstable
public class DataFrame {
    Lists<DFRow> t;
    Lists<String> columns;
    Integer rowSize;

    Maps<String, Integer> column2index;

    private Sets<Integer> noNumberColumnIndices = Sets.empty();
    private Sets<String> noNumberColumns = Sets.empty();
    List<Integer> indices = new ArrayList<>();
    private Lists<ColumnCast> columnCasts = Lists.empty();


    /**
     * @param t each item in t is a row in a table
     */
    public static DataFrame fromLists(Lists<Lists<Object>> t) {
        Lists<DFRow> rows = t.mapIndexed((idx, ls) -> new DFRow(idx).setValues(ls));
        DataFrame df = new DataFrame(rows);
        df.t.forEach(r -> r.setDf(df));
        df.initColumns();
        return df;
    }

    DataFrame(Lists<DFRow> rows) {
        this.rowSize = DataFrame.checkTable(rows);
        Lists<DFRow> columnWise = DataFrame.transpose(rows);
        Lists<DFRow> castedColumnWise = Lists.empty();
        for (DFRow column : columnWise.get()) {
            Pair<ColumnCast, DFRow> casted = column.cast();
            castedColumnWise.add(casted.v());
            this.columnCasts.add(casted.k());
        }
        this.t = DataFrame.transpose(castedColumnWise);
        this.t.forEach(row -> row.setDf(this));
        this.initColumns();
    }

    private void initColumns() {
        if (this.rowSize > 0)
            this.columns = Ranges.of(this.rowSize).ls().map(i -> "Column" + i);
        else
            this.columns = Lists.empty();
        this.column2index = this.columns.associateIndexed((idx, c) -> Pair.of(c, idx));
    }

    public static DataFrame fromCsv(File csvFile, String delimiter) {
        Try.supply(() -> Files.readAllLines(csvFile.toPath()));
        DataFrame df = Try.with(() -> Files.readAllLines(csvFile.toPath())).function(strings -> {
                    Lists<Lists<Object>> ss = Lists.wrap(strings).map(s -> Lists.of(s.split(delimiter)).cast(Object.class));
                    Lists<String> columnNames = ss.first().map(Object::toString);
                    Lists<Lists<Object>> content = ss.drop(1);
                    content.map(os -> os.addAll(columnNames.size() - os.size() > 0 ? Ranges.of(columnNames.size() - os.size()).ls().map(x -> null) : Lists.empty())); // fill up missing values
                    DataFrame d = DataFrame.fromLists(content).setColumns(columnNames);
                    return d;
                }
        );
        return df;
    }

    public static DataFrame fromCsv2(File csvFile, String delimiter) {
        DataFrame df = Try.with(() -> TextReader.read(csvFile)).function(lines -> {
                    Lists<String> columnNames = Lists.of(lines.first().split(delimiter));
                    Lists<String> body = lines.drop(1);
                    Lists<Lists<Object>> content = body.map(s -> Lists.of(s.split(delimiter)).cast(Object.class));
                    content.map(os -> os.addAll(columnNames.size() - os.size() > 0 ? Ranges.of(columnNames.size() - os.size()).ls().map(x -> null) : Lists.empty())); // fill up missing values
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
        if (t.isEmpty())
            return Lists.empty();
        Lists<DFRow> columnWise = Ranges.of(rowSize).ls().map(integer -> new DFRow(integer));
        for (int columnIndex = 0; columnIndex < rowSize; columnIndex++) {
            for (int rowIndex = 0; rowIndex < t.size(); rowIndex++) {
                columnWise.get(columnIndex).addValue(t.get(rowIndex).getObject(columnIndex));
            }
        }
        return columnWise;
    }

    public DataFrame transpose() {
        return new DataFrame(DataFrame.transpose(this.t));
    }

    /**
     * check whether all rows have the same amount of columns
     *
     * @param t
     * @return
     */
    private static Integer checkTable(Lists<DFRow> t) {
        if (t.isEmpty())
            return 0;
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
        Maps<String, Integer> columnNameCounts = columns.associate(s -> Pair.of(s, 0));
        columns = columns.map(originalName -> {
            String c = originalName;
            int count = columnNameCounts.get(originalName);
            if (count > 0) {
                String newColumnName = originalName + "_" + count;
                System.out.println("Duplicate column name. Will rename '" + originalName + "' to '" + newColumnName + "'.");
                c = newColumnName;
            }
            count++;
            columnNameCounts.put(originalName, count);
            return c;
        });
        this.columns = columns;
        this.column2index = this.columns.associateIndexed((integer, s) -> Pair.of(s, integer));
        return this;
    }

    public DataFrame setColumns(String... columns) {
        return this.setColumns(Lists.of(columns));
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
    @Deprecated
    DataFrame setNoNumberColumnIndices(Sets<Integer> noNumberColumns) {
        this.noNumberColumnIndices = noNumberColumns;
        return this;
    }

    public DataFrame setNoNumberColumns(Sets<String> noNumberColumns) {
        this.noNumberColumns = noNumberColumns;
        this.noNumberColumnIndices = this.noNumberColumns.map(c -> this.column2index.get(c));
        return this;
    }

    public DataFrame setNoNumberColumns(String... noNumberColumns) {
        return this.setNoNumberColumns(Sets.of(noNumberColumns));
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

    /**
     * Selects columns by name.
     *
     * @param columnNames
     * @return
     */
    public Selection select(String... columnNames) {
        Selection selection = Selection.of(this, columnNames);
        this.checkColumnNames(columnNames);
        return selection;
    }

    /**
     * Selects all columns.
     *
     * @return
     */
    public Selection select() {
        Selection selection = Selection.of(this, this.columns.mapIndexed((integer, s) -> integer));
        return selection;
    }

    /**
     * Selects columns by name.
     *
     * @param columnNames
     * @return
     */
    public Selection select(Lists<String> columnNames) {
        String[] columnNamesArr = new String[columnNames.size()];
        columnNames.forEachIndexed((idx, s) -> columnNamesArr[idx] = s);
        Selection selection = Selection.of(this, columnNamesArr);
        this.checkColumnNames(columnNames);
        return selection;
    }

    /**
     * Selects columns by index.
     *
     * @param columnIndices
     * @return
     */
    public Selection select(Integer... columnIndices) {
        Selection selection = Selection.of(this, Lists.of(columnIndices));
        return selection;
    }

    /**
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
        Sets<String> columnsToKeep = this.columns.sets().subtract(Sets.of(columns));
        return this.keep(columnsToKeep);
    }

    public DataFrame keep(String... columns) {
        return this.keep(Sets.of(columns));
    }

    public DataFrame keep(Sets<String> columnsToKeepSet) {
        this.checkColumnNames(columns);
        Sets<Integer> columnIndicesToKeep = columnsToKeepSet.map(c -> this.column2index.get(c));
        Lists<String> columnsToKeep = this.columns.filter(columnsToKeepSet::contains);
        Sets<String> noNumberColumnsToKeep = this.noNumberColumns.filter(columnsToKeepSet::contains);
        Lists<DFRow> newT = Lists.empty();
        this.t.forEachIndexed((idx, row) -> newT.add(new DFRow(idx).setDf(this).setValues(row.filterIndexed((integer, o) -> columnIndicesToKeep.contains(integer)))));
        return new DataFrame(newT).setColumns(columnsToKeep).setNoNumberColumns(noNumberColumnsToKeep);
    }

    public Lists<DFRow> getRows() {
        return this.t;
    }

    public DFRow getRow(int rowIdx) {
        return this.t.get(rowIdx);
    }

    public DataFrame print() {
        return this.print(null, 10);
    }

    public DataFrame printAll(String title) {
        this.print(title, null);
        return this;
    }

    public DataFrame print(String title) {
        return this.print(title, 10);
    }

    public DataFrame print(String title, Integer noOfRows) {
        Lists<Integer> columnPaddings = Lists.empty();
        Lists<String> columns = this.columns.mapIndexed((idx, columnName) -> {
            Lists<DFValue> columnValues = this.getColumn(idx);
            Lists<Integer> maxColumnSizes = columnValues.filter(dfValue -> !dfValue.isNull()).map(dfValue -> dfValue.toString().length()).sort(Integer::compareTo);
            int maxLength = maxColumnSizes.isEmpty() ? 0 : maxColumnSizes.last();
            maxLength = Math.max(maxLength, this.columnCasts.get(idx).getPrintableName(this.getColumn(idx)).length());
            maxLength = Math.max(maxLength, columnName.length());
            int padding = Math.max(5, maxLength);
            columnPaddings.add(padding);
            return Strings.rightPad(columnName, padding, " ");
        });
        Lists<Integer> columnCharCount = columns.map(String::length);
        String join = "| " + columns.map(c -> c + " | ").join("");
        final String bars = Lists.of("-").repeat(join.length()).join("");
        if (title != null) {
            System.out.println(bars);
            System.out.println(DataFrame.fillStr("| " + title + " ", bars.length() - 2) + "| ");
        }
        String types = "| " + this.columnCasts.mapIndexed((idx, columnCast) -> Strings.rightPad(columnCast.getPrintableName(this.getColumn(idx)), columnPaddings.get(idx), " ") + " | ").join("");
        System.out.println(bars);
        System.out.println(join);
        System.out.println(bars);
        System.out.println(types);
        System.out.println(bars);
        Lists<Integer> absIndices = Lists.empty();
        if (noOfRows != null && this.t.size() > noOfRows) {
            Lists<Integer> first8 = this.t.take(8).mapIndexed((idx, ignore) -> idx);
            absIndices.addAll(first8);
            absIndices.add(this.t.size() - 1);
        } else {
            absIndices = Ranges.of(this.t.size()).ls();
        }
        absIndices.forEachIndexed((count, rowIndex) -> {
            DFRow row = this.t.get(rowIndex);
            String rowString = "| " + row.mapIndexed((colIdx, o) -> o.isNull() ? "null" : o.toString()).mapIndexed((colIdx, s) -> DataFrame.fillStr(s, columnCharCount.get(colIdx)) + " | ").join("");
            if (noOfRows != null && count == noOfRows - 2 && this.t.size() > noOfRows && join.length() > 5) {
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

    public Lists<DFValue> getColumn(String column) {
        Integer idx = this.column2index.get(column);
        return this.t.map(row -> row.get(idx));
    }

    public Boolean hasColumn(String column) {
        return this.column2index.containsKey(column);
    }

    public Integer getColumnIndex(String column) {
        this.checkColumnNames(column);
        return this.column2index.get(column);
    }

    public Lists<DFValue> getColumn(Integer idx) {
        return this.t.map(row -> row.get(idx));
    }

    public DataFrame takeRows(int n) {
        Lists<Lists<Object>> t = this.getRows().take(n).map(dfRow -> dfRow.getValues().map(DFValue::getObject));
        return DataFrame.fromLists(t) //
                .setColumns(this.getColumns());
    }

    public DataFrame addColumn(String columnName) {
        return this.addColumn(columnName, null);
    }

    public DataFrame addColumn(String columnName, Object value) {
        Lists<Lists<Object>> t = this.getRows().map(dfRow -> dfRow.getValues().map(DFValue::getObject));
        Lists<String> columns = this.getColumns().add(columnName);
        t.forEach(row -> row.add(value));
        DataFrame df = DataFrame.fromLists(t).setColumns(columns);
        return df;
    }

    private void checkNewColumnNames(Lists<String> newColumnNames) {
        Sets<String> existing = this.columns.sets();
        Checks.check("Got a duplicate column name when trying to add these columns: '" + newColumnNames + "' to these columns '" + this.columns + "'", () -> newColumnNames.allMatch(nc -> !existing.contains(nc)));
    }

    public <X> DataFrame computeColumn(String columnName, Integer columnIndex, ActionFunction<DFRow, X> f) {
        Checks.check("Column name is null", () -> columnName != null);
        this.checkNewColumnNames(Lists.of(columnName));
        Lists<X> columnValues = this.t.map(f::apply);
        Lists<DFRow> rows;
        columnIndex = columnIndex == null ? this.column2index.size() : columnIndex;
        Integer finalColumnIndex = columnIndex;
        if (columnIndex >= this.column2index.size()) {
            rows = this.t.mapIndexed((rowIdx, dfRow) -> {
                Lists<Object> values = dfRow.getValues().map(DFValue::getObject);
                values.add(columnValues.get(rowIdx));
                return new DFRow(rowIdx).setValues(values);
            });
        } else {
            rows = this.t.mapIndexed((rowIdx, dfRow) -> {
                Lists<Object> values = dfRow.getValues().map(DFValue::getObject);
                values.insert(finalColumnIndex, columnValues.get(rowIdx));
                return new DFRow(rowIdx).setValues(values);
            });
        }
        Lists<String> columns = this.columns.copy().insert(columnIndex, columnName);
        return new DataFrame(rows).setColumns(columns);
    }

    public <X> DataFrame computeColumn(String columnName, ActionFunction<DFRow, X> f) {
        return this.computeColumn(columnName, null, f);
    }

    public DataFrame computeColumns(Lists<String> columnNames, Integer columnIndex, ActionFunction<DFRow, Lists<Object>> f) {
        Checks.check("No column names provided.", () -> columnNames != null && columnNames.notEmpty());
        Checks.check("Column name is null.", () -> columnNames.allMatch(Objects::nonNull));
        this.checkNewColumnNames(columnNames);
        Lists<Lists<Object>> columnValues = this.t.mapIndexed((idx, dfRow) -> {
            Lists<Object> values = f.apply(dfRow);
            Checks.check("Expected " + columnNames.size() + " new values, but got " + values.size() + " instead. Happened while computing columns for row " + idx + ": " + dfRow, () -> values.size() == columnNames.size());
            return values;
        });
        Lists<DFRow> rows;
        columnIndex = columnIndex == null ? this.column2index.size() : columnIndex;
        Integer finalColumnIndex = columnIndex;
        if (columnIndex >= this.column2index.size()) {
            rows = this.t.mapIndexed((rowIdx, dfRow) -> {
                Lists<Object> values = dfRow.getValues().map(DFValue::getObject);
                values.addAll(columnValues.get(rowIdx));
                return new DFRow(rowIdx).setValues(values);
            });
        } else {
            rows = this.t.mapIndexed((rowIdx, dfRow) -> {
                Lists<Object> values = dfRow.getValues().map(DFValue::getObject);
                values.insert(finalColumnIndex, columnValues.get(rowIdx));
                return new DFRow(rowIdx).setValues(values);
            });
        }

        Lists<String> columns = this.columns.copy().insert(columnIndex, columnNames);
        return new DataFrame(rows).setColumns(columns);
    }

    public DataFrame computeColumns(Lists<String> columnNames, ActionFunction<DFRow, Lists<Object>> f) {
        return this.computeColumns(columnNames, null, f);
    }

    public void toCsv(File file, String delimiter) {
        delimiter = delimiter == null ? ";" : delimiter;
        StringBuilder b = new StringBuilder();
        b.append(this.columns.join(delimiter)).append("\n");
        String finalDelimiter = delimiter;
        this.t.forEach(dfRow -> b.append(dfRow.getValues().map(dfValue -> dfValue.isNull() ? "" : dfValue.getObject()).join(finalDelimiter)).append("\n"));
        try {
            Files.writeString(file.toPath(), b.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DataFrame compute(ActionConsumer<DataFrame> f) {
        f.accept(this);
        return this;
    }

    public DataFrame copy() {
        return new DataFrame(this.getRows().mapIndexed((idx, dfRow) -> new DFRow(idx).setValues(dfRow.getValues().map(DFValue::getObject))));
    }

    public DataFrame addRow(Object... objs) {
        Checks.check("No values provided.", () -> objs != null && objs.length > 0);
        Checks.check("Wrong amount of values provided.", () -> objs.length == this.columns.size());
        Lists<Object> objects = Lists.of(objs);
        Checks.check("Wrong type.", () -> objects.zip(this.columnCasts).forEach(p -> p.v().apply(p.k())).ok());
        this.t.add(new DFRow(this.t.size()).setValues(objects).setDf(this));
        return this;
    }
}
