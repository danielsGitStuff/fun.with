package fun.with.lists;

import fun.with.Lists;
import fun.with.Pair;
import fun.with.lists.classes.BaseTest;
import fun.with.lists.classes.House;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ListsTest extends BaseTest {
    @Test
    public void unique() {
        Lists<House> ls = this.housesWithReplicas.unique();
        assertEquals(ls, Lists.of(new House(2, 4), new House(3, 5)));
    }

    @Test
    public void zip() {
        Lists<Pair<House, Integer>> zipped = this.houses.zip(houseNumbers);
        assertEquals(this.houses.size(), zipped.size());
        zipped.forEachIndexed((index, houseIntegerPair) -> {
            House house = houseIntegerPair.k();
            Integer number = houseIntegerPair.v();
            assertEquals(index + 1, number);
            assertEquals(index + 2, house.doors);
            assertEquals(index + 4, house.windows);
        });
    }

    @Test
    public void permute() {
        Lists<Lists<Integer>> ps = Lists.of(1, 2).permute().withoutRepetition(2);
        ps.forEachIndexed((index, ls) -> {
            System.out.println(ls);
            // alternate between 1 and 2
            int expectedFirst = index % 2 + 1;
            int expectedSecond = (int) Math.ceil((index + 1) / 2.0);
            assertEquals(expectedFirst, ls.first());
            assertEquals(expectedSecond, ls.last());
            assertEquals(2, ls.size());
        });
    }

    @Test
    public void toStr() {
        assertEquals("[1,2]", Lists.of(1, 2).toString());
    }

    @Test
    public void first() {
        assertEquals(1, Lists.of(1, 2, 3).first());
    }

    @Test
    public void last() {
        assertEquals(3, Lists.of(1, 2, 3).last());
    }

    @Test
    public void reverse() {
        houseNumbers.reverse().forEachIndexed((index, number) -> {
            System.out.println(number);
            int expected = houseNumbers.size() - index;
            assertEquals(expected, number);
        });
    }

    @Test
    public void sortBy() {
        this.houses.reverse().sortBy(house -> house.doors).forEachIndexed((index, house) ->
                assertEquals(this.houses.get(index), house));
    }

    @Test
    public void flatMap() {
        Lists<Integer> ls = Lists.of(1, 2, 3).flatMap(n -> Lists.of(n).repeat(3));
        assertEquals(9, ls.size());
        ls.forEachIndexed((index, n) -> {
            int expected = Math.floorDiv(index, 3) + 1;
            assertEquals(expected, n);
        });
    }

    @Test
    public void forEach() {
        AtomicInteger index = new AtomicInteger();
        Lists.of(1, 2, 3).forEach(n -> {
                    assertEquals(index.get() + 1, n);
                    index.getAndIncrement();
                }
        );
    }

    @Test
    public void forEachIndexed() {
        AtomicInteger index = new AtomicInteger();
        Lists.of(1, 2, 3).forEachIndexed((i, n) -> {
                    assertEquals(index.get() + 1, n);
                    assertEquals(index.get(), i);
                    index.getAndIncrement();
                }
        );
    }

    @Test
    public void isEmpty() {
        assertTrue(Lists.empty().isEmpty());
        assertTrue(Lists.wrap(new ArrayList<>()).isEmpty());
        assertFalse(Lists.wrap(Arrays.asList(1, 2, 3)).isEmpty());
        assertFalse(Lists.of(1, 2, 3).isEmpty());
    }

    @Test
    void wrap() {
    }

    @Test
    void mapIndexed() {
    }

    @Test
    void get() {
    }

    @Test
    void testWrap() {
    }

    @Test
    void testWrap1() {
    }

    @Test
    void testWrap2() {
    }

    @Test
    void of() {
    }

    @Test
    void join() {
    }

    @Test
    void empty() {
    }

    @Test
    void testGet() {
    }

    @Test
    void take() {
    }

    @Test
    void subList() {
    }

    @Test
    void filter() {
    }

    @Test
    void add() {
    }

    @Test
    void addAll() {
    }

    @Test
    void testAddAll() {
    }

    @Test
    void addTo() {
    }

    @Test
    void iterator() {
    }

    @Test
    void size() {
    }

    @Test
    void contains() {
    }

    @Test
    void map() {
    }

    @Test
    void associate() {
    }

    @Test
    void associateBy() {
    }

    @Test
    void associateWith() {
    }

    @Test
    void testAssociateWith() {
    }

    @Test
    void groupBy() {
    }

    @Test
    void testGroupBy() {
    }

    @Test
    void sort() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testToString() {
    }

    @Test
    void repeat() {
    }

    @Test
    void sets() {
    }
}
