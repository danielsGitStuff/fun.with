package fun.with.dataframe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataFrameTest {

    @Test
    void fillStr() {
        assertEquals(5, DataFrame.fillStr("1234", 5).length());
        assertEquals(4, DataFrame.fillStr("1234", 4).length());
        assertEquals(3, DataFrame.fillStr("1234", 3).length());
    }
}
