package fun.with.lists;

import fun.with.Lists;

import static org.junit.jupiter.api.Assertions.*;

import fun.with.Maps;
import fun.with.Pair;
import fun.with.lists.classes.BaseTest;
import fun.with.lists.classes.House;
import org.junit.jupiter.api.Test;

public class AssociationTests extends BaseTest {

    @Test
    public void testFlatMap() {
        assertEquals(this.houses.flatMap(house -> Lists.of(house.doors, 9)), Lists.of(2, 9, 3, 9, 4, 9));
    }

    @Test
    public void testAssociate() {
        Maps<Integer, House> m = this.houses.associate(house -> Pair.of(house.doors, house));
        assertEquals(m.get(2), new House(2, 4));
        assertEquals(m.get(3), new House(3, 5));
        assertEquals(m.get(4), new House(4, 6));
    }

    @Test
    public void testAssociateBy() {
        Maps<Integer, House> m = this.houses.associateBy(house -> house.doors);
        assertEquals(m.get(2), new House(2, 4));
        assertEquals(m.get(3), new House(3, 5));
        assertEquals(m.get(4), new House(4, 6));
    }

    @Test
    public void testAssociateWith() {
        Maps<House, Integer> m = this.houses.associateWith(house -> house.doors);
        assertEquals(m.get(new House(2, 4)), 2);
        assertEquals(m.get(new House(3, 5)), 3);
        assertEquals(m.get(new House(4, 6)), 4);
    }
}
