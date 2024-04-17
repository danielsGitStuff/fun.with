package fun.with;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RangesTest {
    @Test
    public void normal() {
        Lists<Integer> ls = Ranges.of(0, 6, 1).ls();
        assertEquals(6, ls.size());
        ls.forEachIndexed(Assertions::assertEquals);
    }

    @Test
    public void step() {
        Lists<Integer> ls = Ranges.of(0, 5, 2).ls();
        assertEquals(3, ls.size());
        ls.forEachIndexed((index, i) -> assertEquals(index * 2, i));
    }

    @Test
    public void test1() {
        Lists<Integer> ints = Ranges.of(0, -6).ls();
        Lists<Integer> intsPos = Ranges.of(0, 6).ls();
        assertEquals(6, ints.size());
        assertEquals(6, intsPos.size());
        ints.forEachIndexed((idx, number) -> assertEquals(-number, intsPos.get(idx)));
    }
}
