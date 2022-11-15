package fun.with.lists;

import fun.with.Lists;
import fun.with.lists.classes.BaseTest;
import fun.with.lists.classes.House;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListsTest extends BaseTest {
    @Test
    public void unique() {
        Lists<House> ls = this.housesWithReplicas.unique();
        assertEquals(ls, Lists.of(new House(2, 4), new House(3, 5)));
    }
}
