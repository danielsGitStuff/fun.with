package fun.with.unstable.dataframe;

import fun.with.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DataFrameTest {
    DataFrame df;

    @BeforeEach
    public void setUp() {
        Lists<Lists<Object>> rows = Lists.of(Lists.of(1, 2, "A"), Lists.of(3, 4, "B"));
        this.df = DataFrame.fromLists(rows).setColumns("n1", "n2", "s1");
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
}