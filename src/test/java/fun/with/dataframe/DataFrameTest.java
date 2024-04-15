package fun.with.dataframe;

import fun.with.Lists;
import fun.with.misc.Range;
import fun.with.unstable.dataframe.DataFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataFrameTest {

    DataFrame df1, dfWithStrings;

    @BeforeEach
    public void before() {
        Lists<Lists<Object>> t = Range.of(4).ls().map(i -> Range.of(i, i + 2).ls().cast(Object.class));
        df1 = DataFrame.fromLists(t);
        t = Range.of(4).ls().map(i -> Lists.of(i, "a" + i));
        dfWithStrings = DataFrame.fromLists(t);
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
}
