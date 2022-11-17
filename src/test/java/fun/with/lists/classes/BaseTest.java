package fun.with.lists.classes;

import fun.with.Lists;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    protected Lists<House> housesWithReplicas;
    protected Lists<House> houses;

    @BeforeEach
    public void beforeEach() {
        this.housesWithReplicas = Lists.of(new House(2, 4), new House(2, 4), new House(3, 5));
        this.houses = Lists.of(new House(2, 4), new House(3, 5), new House(4, 6));
    }
}
