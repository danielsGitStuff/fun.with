package fun.with.unstable.dataframe;

import fun.with.Lists;
import fun.with.Ranges;
import fun.with.exampledata.House;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataFrameTest {
    DataFrame df;
    DataFrame df1, dfWithStrings;


    @BeforeEach
    public void setUp() {
        Lists<Lists<Object>> rows = Lists.of(Lists.of(1, 2, "A"), Lists.of(3, 4, "B"));
        this.df = DataFrame.fromLists(rows).setColumns("n1", "n2", "s1");

        Lists<Lists<Object>> t = Ranges.of(4).ls().map(i -> Ranges.of(i, i + 2).ls().cast(Object.class));
        df1 = DataFrame.fromLists(t);
        t = Ranges.of(4).ls().map(i -> Lists.of(i, "a" + i));
        dfWithStrings = DataFrame.fromLists(t);
    }

    @Test
    void computeColumn1() {
        DataFrame df = this.df.computeColumn("sum", dfRow -> dfRow.get("n1").getInt() + dfRow.get("n2").getInt());
        System.out.println("DataFrameTest.computeColumn");
        df.checkColumnNames("n1", "n2", "sum", "s1");
        assertEquals(3, df.getColumnIndex("sum"));
        assertEquals(3, df.getColumn("sum").get(0).getInt());
        assertEquals(7, df.getColumn("sum").get(1).getInt());
    }

    @Test
    void computeColumn2() {
        DataFrame df = this.df.computeColumn("sum", 2, dfRow -> dfRow.get("n1").getInt() + dfRow.get("n2").getInt());
        System.out.println("DataFrameTest.computeColumn");
        df.checkColumnNames("n1", "n2", "sum", "s1");
        assertEquals(2, df.getColumnIndex("sum"));
        assertEquals(3, df.getColumn("sum").get(0).getInt());
        assertEquals(7, df.getColumn("sum").get(1).getInt());
        df.printAll("sum");
    }

    @Test
    void fillStr() {
        assertEquals(5, DataFrame.fillStr("1234", 5).length());
        assertEquals(4, DataFrame.fillStr("1234", 4).length());
        assertEquals(3, DataFrame.fillStr("1234", 3).length());
    }

    @Test
    void testGeneral1() {
        df1.print();
        assertEquals(0, df1.getRows().first().get("Column0").getInt());
        assertEquals(1, df1.getRows().first().get("Column1").getInt());
        assertEquals(4, df1.getRows().last().last().getInt());
    }

    @Test
    void testTranspose1() {
        DataFrame df = df1.transpose();
        df1.print();
        df.print();
        assertEquals(0, df.getRows().first().get("Column0").getInt());
        assertEquals(3, df.getRows().first().get("Column3").getInt());
        assertEquals(4, df.getRows().last().last().getInt());
    }

    @Test
    void testSelect1() {
        DataFrame selected = df1.select(0).filter(dfvs -> dfvs.first().getInt() > 1).df();
        selected.print();
        assertEquals(2, selected.getRows().first().first().getInt());
    }

    @Test
    public void testComputeColumn1() {
        Lists<Lists<Object>> houseRows = Lists.of(Lists.of(new House(2, 3)).cast(Object.class), Lists.of(new House(5, 6)).cast(Object.class));
        DataFrame df = DataFrame.fromLists(houseRows).setColumns("house");
        df = df.computeColumn("doors", dfRow -> dfRow.get("house").getCast(House.class).doors);
        df = df.computeColumn("windows", dfRow -> dfRow.get("house").getCast(House.class).windows);
        df = df.computeColumn("holes", dfRow -> dfRow.get("doors").getInt() + dfRow.get("windows").getInt());
        df.printAll("test");
        assertEquals(4, df.getColumns().size());
        assertEquals(2, df.getRows().size());
        assertEquals(5, df.getColumn("holes").first().getInt());
        assertEquals(11, df.getColumn("holes").second().getInt());
    }

    @Test
    public void testComputeColumns1() {
        Lists<Lists<Object>> houseRows = Lists.of(Lists.of(new House(2, 3)).cast(Object.class), Lists.of(new House(5, 6)).cast(Object.class));
        DataFrame df = DataFrame.fromLists(houseRows).setColumns("house");
        df = df.computeColumns(Lists.of("doors", "windows", "holes"), row -> Lists.of(row.get("house").getCast(House.class).doors, row.get("house").getCast(House.class).windows, row.get("house").getCast(House.class).doors + row.get("house").getCast(House.class).windows));
        df = df.computeColumns(Lists.of("holes_2", "holes_3"), 1, dfRow -> Lists.of(dfRow.get("holes").getInt() * dfRow.get("holes").getInt(), dfRow.get("holes").getInt() * dfRow.get("holes").getInt() * dfRow.get("holes").getInt()));
        df.printAll("test");
        assertEquals(6, df.getColumns().size());
        assertEquals(2, df.getRows().size());
        assertEquals(5, df.getColumn("holes").first().getInt());
        assertEquals(11, df.getColumn("holes").second().getInt());
        Lists<String> expectedColumns = Lists.of("house", "holes_2", "holes_3", "doors", "windows", "holes");
        DataFrame finalDf = df;
        expectedColumns.forEachIndexed((columnIdx, expectedColumn) -> assertEquals(expectedColumn, finalDf.getColumns().get(columnIdx)));
    }

    @Test
    public void testCompute1(){
        df.printAll("start");
        df.addRow(1,3,"B");
        DataFrame changed3 = df.select().filterRows(dfRow -> dfRow.get("n1").eq(1)).df()
                .compute(dataFrame -> dataFrame.select().filterRows(dfRow -> dfRow.get("n2").eq(2)).getRows()
                        .forEach(dfRow -> dfRow.get("s1").setValue("DD")));
        changed3.printAll("test");
        assertEquals(2, changed3.getRows().size());
        assertEquals("DD", changed3.getRow(0).get("s1").getString());
        assertEquals("B", changed3.getRow(1).get("s1").getString());
    }
}