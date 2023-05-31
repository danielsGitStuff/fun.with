package fun.with.lists;

import fun.with.*;
import fun.with.lists.classes.BaseTest;
import fun.with.lists.classes.House;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        assertEquals("l(2)[1,2]", Lists.of(1, 2).toString());
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
        List<Integer> expectedIndices = new ArrayList<>();
        List<House> expectedHouses = new ArrayList<>();
        for (int i = 0; i < this.houses.size(); i++) {
            expectedIndices.add(i);
            expectedHouses.add(this.houses.get(i));
        }
        this.houses.copy().mapIndexed((i, h) -> {
            assertEquals(h, expectedHouses.get(i));
            return i;
        }).forEach(i -> assertEquals(expectedIndices.get(i), i));
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
        Lists<Integer> numbers = Range.of(0, 6).ls().take(2);
        assertEquals(2, numbers.size());
        numbers.forEachIndexed(Assertions::assertEquals);
    }

    @Test
    void subList() {
        Lists<Integer> numbers = Range.of(1, 6).ls();
        Lists<Integer> subList = numbers.subList(2, 3);
        assertEquals(1, subList.size());
        assertEquals(3, subList.first());
    }

    @Test
    void filter() {
        Lists<Integer> filtered = Lists.of(1, 2, 3, 4, 5).filter(integer -> integer != 3);
        assertFalse(filtered.contains(3));
        assertEquals(4, filtered.size());
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
        assertEquals(3, houseNumbers.size());
    }

    @Test
    void contains() {
        assertFalse(houseNumbers.contains(0));
        assertTrue(houseNumbers.contains(1));
        assertTrue(houseNumbers.contains(2));
        assertTrue(houseNumbers.contains(3));
        assertFalse(houseNumbers.contains(4));
    }

    @Test
    void drop() {
        Lists<Integer> dropped1 = houseNumbers.drop(1);
        Lists<Integer> dropped3 = houseNumbers.drop(3);
        assertEquals(Lists.of(2, 3), dropped1);
        assertEquals(Lists.empty(), dropped3);
    }

    @Test
    void map() {
        Lists<Integer> numbers = Lists.of(1, 2, 3).map(i -> i - 1);
        numbers.forEachIndexed(Assertions::assertEquals);
        assertEquals(3, numbers.size());
    }

    @Test
    void groupBy() {
        Lists<House> houses = this.houses.copy().add(new House(2, 9));
        Maps<Integer, Lists<Integer>> windowsGroupedByDoors = houses.groupBy(h -> h.doors, house -> house.windows);
        assertEquals(3, windowsGroupedByDoors.size());
        assertEquals(1, windowsGroupedByDoors.get(3).size());
        assertEquals(1, windowsGroupedByDoors.get(4).size());
    }

    @Test
    void testGroupBy() {
        Lists<House> houses = this.houses.copy().add(new House(2, 9));
        Maps<Integer, Lists<House>> groupedByDoors = houses.groupBy(h -> h.doors);
        assertEquals(3, groupedByDoors.size());
        assertEquals(1, groupedByDoors.get(3).size());
        assertEquals(1, groupedByDoors.get(4).size());
    }

    @Test
    void sort() {
        Lists<Integer> sorted = Lists.of(3, 2, 1, 0).sort(Integer::compare);
        sorted.forEachIndexed(Assertions::assertEquals);
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
        assertEquals(this.houseNumbers.size() * 2, this.houseNumbers.repeat(2).size());
    }

    @Test
    void sets() {
        Sets<Integer> s = Lists.of(1, 2, 3).sets();
        Range.of(1, 3).ls().forEach(i -> assertTrue(s.contains(i)));
    }
}
