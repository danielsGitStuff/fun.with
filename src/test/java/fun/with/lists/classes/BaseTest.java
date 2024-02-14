package fun.with.lists.classes;

import fun.with.Lists;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    protected Lists<House> housesWithReplicas, housesWithSameDoors;
    protected Lists<House> houses;

    protected Lists<Integer> houseNumbers;

    @BeforeEach
    public void beforeEach() {
        this.housesWithReplicas = Lists.of(new House(2, 4), new House(2, 4), new House(3, 5));
        this.housesWithSameDoors = Lists.of(new House(2, 4), new House(2, 5), new House(3, 6));
        this.houses = Lists.of(new House(2, 4), new House(3, 5), new House(4, 6));
        this.houseNumbers = houses.mapIndexed((integer, house) -> integer + 1);
    }
}
