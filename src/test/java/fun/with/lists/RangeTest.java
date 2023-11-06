package fun.with.lists;

import fun.with.Lists;
import fun.with.Range;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RangeTest {
    @Test
    public void normal() {
        Lists<Integer> ls = Range.of(0, 6, 1).ls();
        assertEquals(6, ls.size());
        ls.forEachIndexed(Assertions::assertEquals);
    }

    @Test
    public void step() {
        Lists<Integer> ls = Range.of(0, 5, 2).ls();
        assertEquals(3, ls.size());
        ls.forEachIndexed((index, i) -> assertEquals(index * 2, i));
    }
}
