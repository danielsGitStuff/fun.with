package fun.with;

import fun.with.lists.classes.BaseTest;
import fun.with.lists.classes.House;
import fun.with.misc.Pair;
import fun.with.misc.Range;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ListsTest extends BaseTest {
    @Test
    public void testUnique() {
        Lists<House> ls = this.housesWithReplicas.unique();
        assertEquals(ls, Lists.of(new House(2, 4), new House(3, 5)));
    }

    @Test
    public void testZip() {
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
    public void testPermute() {
        Lists<Lists<Integer>> ps = Lists.of(1, 2).permute().withRepetition(2);
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
    public void testToStr() {
        assertEquals("l(2)[1,2]", Lists.of(1, 2).toString());
    }

    @Test
    public void testFirst() {
        assertEquals(1, Lists.of(1, 2, 3).first());
    }

    @Test
    public void testLast() {
        assertEquals(3, Lists.of(1, 2, 3).last());
    }

    @Test
    public void testReverse() {
        houseNumbers.reverse().forEachIndexed((index, number) -> {
            System.out.println(number);
            int expected = houseNumbers.size() - index;
            assertEquals(expected, number);
        });
    }

    @Test
    public void testSortBy() {
        this.houses.reverse().sortBy(house -> house.doors).forEachIndexed((index, house) ->
                assertEquals(this.houses.get(index), house));
    }

    @Test
    public void testFlatMap() {
        Lists<Integer> ls = Lists.of(1, 2, 3).flatMap(n -> Lists.of(n).repeat(3));
        assertEquals(9, ls.size());
        ls.forEachIndexed((index, n) -> {
            int expected = Math.floorDiv(index, 3) + 1;
            assertEquals(expected, n);
        });
    }

    @Test
    public void testForEach() {
        AtomicInteger index = new AtomicInteger();
        Lists.of(1, 2, 3).forEach(n -> {
                    assertEquals(index.get() + 1, n);
                    index.getAndIncrement();
                }
        );
    }

    @Test
    public void testForEachIndexed() {
        AtomicInteger index = new AtomicInteger();
        Lists.of(1, 2, 3).forEachIndexed((i, n) -> {
                    assertEquals(index.get() + 1, n);
                    assertEquals(index.get(), i);
                    index.getAndIncrement();
                }
        );
    }

    @Test
    public void testIsEmpty() {
        assertTrue(Lists.empty().isEmpty());
        assertTrue(Lists.wrap(new ArrayList<>()).isEmpty());
        assertFalse(Lists.wrap(Arrays.asList(1, 2, 3)).isEmpty());
        assertFalse(Lists.of(1, 2, 3).isEmpty());
    }

    @Test
    public void testWrap() {
        Lists<House> ls = Lists.wrap(this.houses.get());
        assertEquals(this.houses.size(), ls.size());
        assertTrue(ls.zip(this.houses).allMatch(houseHousePair -> houseHousePair.k() == houseHousePair.v()));
    }

    @Test
    public void testMapIndexed() {
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
    public void testGet() {
    }

    @Test
    public void testWrap1() {
    }

    @Test
    public void testWrap2() {
    }

    @Test
    public void testOf() {
    }

    @Test
    public void testJoin() {
        String s = Lists.of(1, 2, 3).join("?");
        assertEquals("1?2?3", s);
    }

    @Test
    public void testEmpty() {
        assertFalse(Lists.of(1).isEmpty());
        assertTrue(Lists.empty().isEmpty());
        assertTrue(Lists.of(1).drop(1).isEmpty());
    }

    @Test
    public void testTake() {
        Lists<Integer> numbers = Range.of(0, 6).ls().take(2);
        assertEquals(2, numbers.size());
        numbers.forEachIndexed(Assertions::assertEquals);
    }

    @Test
    public void testSubList() {
        Lists<Integer> numbers = Range.of(1, 6).ls();
        Lists<Integer> subList = numbers.subList(2, 3);
        assertEquals(1, subList.size());
        assertEquals(3, subList.first());
    }

    @Test
    public void testFilter() {
        Lists<Integer> filtered = Lists.of(1, 2, 3, 4, 5).filter(integer -> integer != 3);
        assertFalse(filtered.contains(3));
        assertEquals(4, filtered.size());
    }

    @Test
    public void testAdd() {
    }

    @Test
    public void testAddAll() {
    }

    @Test
    public void testAddTo() {
    }

    @Test
    public void testIterator() {
    }

    @Test
    public void testSize() {
        assertEquals(3, houseNumbers.size());
    }

    @Test
    public void testContains() {
        assertFalse(houseNumbers.contains(0));
        assertTrue(houseNumbers.contains(1));
        assertTrue(houseNumbers.contains(2));
        assertTrue(houseNumbers.contains(3));
        assertFalse(houseNumbers.contains(4));
    }

    @Test
    public void testDrop() {
        Lists<Integer> dropped1 = houseNumbers.drop(1);
        Lists<Integer> dropped3 = houseNumbers.drop(3);
        assertEquals(Lists.of(2, 3), dropped1);
        assertEquals(Lists.empty(), dropped3);
    }

    @Test
    public void testMap() {
        Lists<Integer> numbers = Lists.of(1, 2, 3).map(i -> i - 1);
        numbers.forEachIndexed(Assertions::assertEquals);
        assertEquals(3, numbers.size());
    }

    @Test
    public void testGroupBy1() {
        Maps<Integer, Lists<House>> doors2house = this.houses.copy().add(new House(2, 9)).groupBy(house -> house.doors);
        assertEquals(3, doors2house.size());
        assertEquals(1, doors2house.get(3).size());
        assertEquals(1, doors2house.get(4).size());
        System.out.println(doors2house);
    }

    @Test
    public void testGroupBy2() {
        Lists<House> houses = this.houses.copy().add(new House(2, 9));
        Maps<Integer, Lists<Integer>> doors2windows = this.houses.copy().add(new House(2, 9)).groupBy(h -> h.doors, house -> house.windows);
        assertEquals(3, doors2windows.size());
        assertEquals(1, doors2windows.get(3).size());
        assertEquals(1, doors2windows.get(4).size());
        System.out.println(doors2windows);
    }

    @Test
    public void testSort() {
        Lists<Integer> sorted = Lists.of(3, 2, 1, 0).sort(Integer::compare);
        sorted.forEachIndexed(Assertions::assertEquals);
    }

    @Test
    public void testHashCode() {
    }

    @Test
    public void testEquals() {
    }

    @Test
    public void testToString() {
    }

    @Test
    public void testRepeat() {
        assertEquals(this.houseNumbers.size() * 2, this.houseNumbers.repeat(2).size());
    }

    @Test
    public void testSets() {
        Sets<Integer> s = Lists.of(1, 2, 3).sets();
        Range.of(1, 3).ls().forEach(i -> assertTrue(s.contains(i)));
    }

    @Test
    public void testView() {
        Lists<Integer> xs = Range.of(1, 10).ls();
        Lists<Lists<Integer>> ys = xs.reshape(3);
        assertEquals(3, ys.size());
        ys.forEachIndexed((rowIndex, ints) -> {
            ints.forEachIndexed((columnIndex, i) -> assertEquals((rowIndex) * 3 + columnIndex + 1, i));
        });
    }

    @Test
    public void testUniqueBy1() {
        Lists<House> uniques = this.housesWithSameDoors.uniqueBy(house -> house.doors, (house1, house2) -> house1);
        assertEquals(2, uniques.size());
        assertEquals(uniques.first(), this.housesWithSameDoors.first());
        assertEquals(uniques.second(), this.housesWithSameDoors.third());
        uniques = this.housesWithSameDoors.uniqueBy(house -> house.doors, (house1, house2) -> house2);
        assertEquals(2, uniques.size());
        assertEquals(uniques.first(), this.housesWithSameDoors.second());
        assertEquals(uniques.second(), this.housesWithSameDoors.third());
    }

    @Test
    public void testAssociateBy() {
        Maps<Integer, House> windows2house = this.houses.associateBy(house -> house.windows);
        assertEquals(3, windows2house.size());
        assertTrue(windows2house.containsKey(4));
        assertTrue(windows2house.containsKey(5));
        assertTrue(windows2house.containsKey(6));
        assertEquals(windows2house.get(4), this.houses.first());
        assertEquals(windows2house.get(5), this.houses.second());
        assertEquals(windows2house.get(6), this.houses.third());
        System.out.println(windows2house);
    }

    @Test
    public void testAssociateWith() {
        System.out.println(this.houses);
        Maps<House, Integer> house2windows = this.houses.associateWith(house -> house.windows);
        assertEquals(3, house2windows.size());
        assertEquals(this.houses.first().windows, house2windows.get(this.houses.first()));
        assertEquals(this.houses.second().windows, house2windows.get(this.houses.second()));
        assertEquals(this.houses.third().windows, house2windows.get(this.houses.third()));
        System.out.println(house2windows);
    }
}
