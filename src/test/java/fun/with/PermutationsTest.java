package fun.with;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermutationsTest {
    private Lists<Integer> ls;

    @BeforeEach
    public void before() {
        this.ls = Lists.of(1, 2, 3);
    }

    @Test
    void withRepetition() {
        Lists<Lists<Integer>> xs = ls.permute().withRepetition(3);
        assertEquals(27, xs.size());
    }
}
